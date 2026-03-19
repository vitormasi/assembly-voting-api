package com.sicredi.assemblyVotingApi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sicredi.assemblyVotingApi.entity.dto.AgendaDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VoteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long openedAgendaId;
    private static Long closedAgendaId;

    @BeforeAll
    void setup() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        AgendaDTO dto = new AgendaDTO();
        dto.setTitle("Pauta para Votos");

        MvcResult result = mockMvc.perform(post("/agenda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        AgendaDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AgendaDTO.class);
        openedAgendaId = response.getId();

        String startTime = LocalDateTime.now().format(formatter);
        String endTime = LocalDateTime.now().plusMinutes(60).format(formatter);

        mockMvc.perform(patch("/agenda/{id}/open", openedAgendaId)
                        .param("startAt", startTime)
                        .param("endAt", endTime))
                .andExpect(status().isOk());

        AgendaDTO closedDto = new AgendaDTO();
        closedDto.setTitle("Pauta Fechada");

        MvcResult closedResult = mockMvc.perform(post("/agenda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(closedDto)))
                .andExpect(status().isCreated())
                .andReturn();

        AgendaDTO closedResponse = objectMapper.readValue(
                closedResult.getResponse().getContentAsString(), AgendaDTO.class);
        closedAgendaId = closedResponse.getId();
    }

    @Test
    @Order(1)
    void shouldVoteYesOnAgenda() throws Exception {
        mockMvc.perform(post("/vote/agenda/{id}", openedAgendaId)
                        .param("cpf", "12345678901")
                        .param("voteEnum", "SIM"))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    void shouldVoteNoOnAgenda() throws Exception {
        mockMvc.perform(post("/vote/agenda/{id}", openedAgendaId)
                        .param("cpf", "98765432100")
                        .param("voteEnum", "NAO"))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @Order(3)
    void shouldNotVoteTwiceWithSameCpf() throws Exception {
        mockMvc.perform(post("/vote/agenda/{id}", openedAgendaId)
                        .param("cpf", "12345678901")
                        .param("voteEnum", "NAO"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(4)
    void shouldNotVoteOnClosedAgenda() throws Exception {
        mockMvc.perform(post("/vote/agenda/{id}", closedAgendaId)
                        .param("cpf", "11122233344")
                        .param("voteEnum", "SIM"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(5)
    void shouldGetCorrectResultAfterVoting() throws Exception {
        mockMvc.perform(get("/agenda/{id}/result", openedAgendaId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(openedAgendaId))
                .andExpect(jsonPath("$.votesCount.SIM").value(1))
                .andExpect(jsonPath("$.votesCount.NAO").value(1));
    }

    @Test
    @Order(6)
    void shouldReturnErrorWhenAgendaNotFound() throws Exception {
        mockMvc.perform(post("/vote/agenda/{id}", 999999L)
                        .param("cpf", "55566677788")
                        .param("voteEnum", "SIM"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    void shouldReturnErrorWhenCpfIsBlankOrInvalid() throws Exception {
        mockMvc.perform(post("/vote/agenda/{id}", openedAgendaId)
                        .param("cpf", "")
                        .param("voteEnum", "SIM"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(8)
    void shouldReturnErrorWhenVoteEnumIsInvalid() throws Exception {
        mockMvc.perform(post("/vote/agenda/{id}", openedAgendaId)
                        .param("cpf", "44455566677")
                        .param("voteEnum", "INVALIDO"))
                .andExpect(status().isInternalServerError());
    }

}
