package com.sicredi.assemblyVotingApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sicredi.assemblyVotingApi.entity.dto.VoteDTO;
import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;
import com.sicredi.assemblyVotingApi.service.VoteService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VoteController.class)
@ActiveProfiles("test")
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VoteService voteService;

    private ObjectMapper objectMapper;

    private VoteDTO voteDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        voteDTO = new VoteDTO();
    }

    @Test
    void shouldCreateVote_withSim_andReturnCreated() throws Exception {
        when(voteService.createVote(1L, "12345678901", VoteEnum.SIM)).thenReturn(voteDTO);

        mockMvc.perform(post("/vote/agenda/{agendaId}", 1L)
                        .param("cpf", "12345678901")
                        .param("voteEnum", "SIM"))
                .andExpect(status().isCreated());

        verify(voteService, times(1)).createVote(1L, "12345678901", VoteEnum.SIM);
    }

    @Test
    void shouldCreateVote_withNao_andReturnCreated() throws Exception {
        when(voteService.createVote(1L, "12345678901", VoteEnum.NAO)).thenReturn(voteDTO);

        mockMvc.perform(post("/vote/agenda/{agendaId}", 1L)
                        .param("cpf", "12345678901")
                        .param("voteEnum", "NAO"))
                .andExpect(status().isCreated());

        verify(voteService, times(1)).createVote(1L, "12345678901", VoteEnum.NAO);
    }

    @Test
    void shouldReturnError_whenCpfIsBlank() throws Exception {
        mockMvc.perform(post("/vote/agenda/{agendaId}", 1L)
                        .param("cpf", "")
                        .param("voteEnum", "SIM"))
                .andExpect(status().isInternalServerError());

        verifyNoInteractions(voteService);
    }

    @Test
    void shouldReturnError_whenCpfIsMissing() throws Exception {
        mockMvc.perform(post("/vote/agenda/{agendaId}", 1L)
                        .param("voteEnum", "SIM"))
                .andExpect(status().isInternalServerError());

        verifyNoInteractions(voteService);
    }

    @Test
    void shouldReturnError_whenVoteEnumIsMissing() throws Exception {
        mockMvc.perform(post("/vote/agenda/{agendaId}", 1L)
                        .param("cpf", "12345678901"))
                .andExpect(status().isInternalServerError());

        verifyNoInteractions(voteService);
    }

    @Test
    void shouldReturnError_whenVoteEnumIsInvalid() throws Exception {
        mockMvc.perform(post("/vote/agenda/{agendaId}", 1L)
                        .param("cpf", "12345678901")
                        .param("voteEnum", "INVALIDO"))
                .andExpect(status().isInternalServerError());

        verifyNoInteractions(voteService);
    }

    @Test
    void shouldReturnNotFound_whenAgendaDoesNotExist() throws Exception {
        when(voteService.createVote(eq(99L), anyString(), any(VoteEnum.class)))
                .thenThrow(new EntityNotFoundException("Pauta não encontrada"));

        mockMvc.perform(post("/vote/agenda/{agendaId}", 99L)
                        .param("cpf", "12345678901")
                        .param("voteEnum", "SIM"))
                .andExpect(status().isNotFound());

        verify(voteService, times(1)).createVote(99L, "12345678901", VoteEnum.SIM);
    }

    @Test
    void shouldReturnError_whenAssociateAlreadyVoted() throws Exception {
        when(voteService.createVote(eq(1L), eq("12345678901"), any(VoteEnum.class)))
                .thenThrow(new IllegalStateException("Associado já votou nesta pauta"));

        mockMvc.perform(post("/vote/agenda/{agendaId}", 1L)
                        .param("cpf", "12345678901")
                        .param("voteEnum", "SIM"))
                .andExpect(status().isInternalServerError());

        verify(voteService, times(1)).createVote(1L, "12345678901", VoteEnum.SIM);
    }

    @Test
    void shouldReturnError_whenAgendaIsClosed() throws Exception {
        when(voteService.createVote(eq(1L), anyString(), any(VoteEnum.class)))
                .thenThrow(new IllegalStateException("Pauta encerrada"));

        mockMvc.perform(post("/vote/agenda/{agendaId}", 1L)
                        .param("cpf", "12345678901")
                        .param("voteEnum", "NAO"))
                .andExpect(status().isInternalServerError());

        verify(voteService, times(1)).createVote(1L, "12345678901", VoteEnum.NAO);
    }

    @Test
    void shouldReturnCreatedWithBody_whenVoteIsCreated() throws Exception {
        voteDTO.setAgendaId(1L);
        voteDTO.setCpf("12345678901");
        voteDTO.setVote(VoteEnum.SIM);

        when(voteService.createVote(1L, "12345678901", VoteEnum.SIM)).thenReturn(voteDTO);

        mockMvc.perform(post("/vote/agenda/{agendaId}", 1L)
                        .param("cpf", "12345678901")
                        .param("voteEnum", "SIM"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.agendaId").value(1L))
                .andExpect(jsonPath("$.vote").value("SIM"));

        verify(voteService, times(1)).createVote(1L, "12345678901", VoteEnum.SIM);
    }
}
