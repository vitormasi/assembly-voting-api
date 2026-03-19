package com.sicredi.assemblyVotingApi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sicredi.assemblyVotingApi.entity.dto.AgendaDTO;
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
class AgendaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long createdAgendaId;

    @Test
    @Order(1)
    void shouldCreateAgenda() throws Exception {
        AgendaDTO dto = new AgendaDTO();
        dto.setTitle("Pauta Integração Completa");

        MvcResult result = mockMvc.perform(post("/agenda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Pauta Integração Completa"))
                .andReturn();

        AgendaDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AgendaDTO.class);
        createdAgendaId = response.getId();
    }

    @Test
    @Order(2)
    void shouldGetAllAgendas() throws Exception {
        mockMvc.perform(get("/agenda")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(3)
    void shouldGetAgendaById() throws Exception {
        mockMvc.perform(get("/agenda/{id}", createdAgendaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdAgendaId))
                .andExpect(jsonPath("$.title").value("Pauta Integração Completa"));
    }

    @Test
    @Order(4)
    void shouldOpenAgendaForVoting() throws Exception {
        mockMvc.perform(patch("/agenda/{id}/open", createdAgendaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdAgendaId));
    }

    @Test
    @Order(5)
    void shouldGetAllOpenedAgendas() throws Exception {
        mockMvc.perform(get("/agenda/opened")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(6)
    void shouldGetAgendaResult() throws Exception {
        mockMvc.perform(get("/agenda/{id}/result", createdAgendaId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdAgendaId));
    }

    @Test
    @Order(7)
    void shouldReturnNotFoundForInvalidId() throws Exception {
        mockMvc.perform(get("/agenda/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(8)
    void shouldReturnBadRequestWhenTitleIsBlank() throws Exception {
        AgendaDTO dto = new AgendaDTO();
        dto.setTitle("");

        mockMvc.perform(post("/agenda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    void shouldReturnErrorWhenOpeningAlreadyOpenedAgenda() throws Exception {
        mockMvc.perform(patch("/agenda/{id}/open", createdAgendaId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(10)
    void shouldReturnEmptyResultForAgendaWithNoVotes() throws Exception {
        mockMvc.perform(get("/agenda/{id}/result", createdAgendaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.votesCount").isEmpty());
    }

}
