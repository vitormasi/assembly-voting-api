package com.sicredi.assemblyVotingApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sicredi.assemblyVotingApi.entity.dto.AgendaDTO;
import com.sicredi.assemblyVotingApi.entity.dto.AgendaResultDTO;
import com.sicredi.assemblyVotingApi.service.AgendaService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgendaController.class)
@ActiveProfiles("test")
class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgendaService agendaService;

    private ObjectMapper objectMapper;
    private AgendaDTO agendaDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        agendaDTO = new AgendaDTO();
        agendaDTO.setTitle("Pauta Teste");
    }

    @Test
    void shouldCreateAgenda_andReturnCreated() throws Exception {
        when(agendaService.create(any(AgendaDTO.class))).thenReturn(agendaDTO);

        mockMvc.perform(post("/agenda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(agendaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Pauta Teste"));

        verify(agendaService, times(1)).create(any(AgendaDTO.class));
    }

    @Test
    void shouldReturnBadRequest_whenCreateAgendaWithInvalidBody() throws Exception {
        mockMvc.perform(post("/agenda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(agendaService);
    }

    @Test
    void shouldGetAllAgendas_andReturnOk() throws Exception {
        Page<AgendaDTO> page = new PageImpl<>(List.of(agendaDTO));
        when(agendaService.getAll(0, 10)).thenReturn(page);

        mockMvc.perform(get("/agenda")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Pauta Teste"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(agendaService, times(1)).getAll(0, 10);
    }

    @Test
    void shouldGetAllAgendas_withDefaultParams_andReturnOk() throws Exception {
        Page<AgendaDTO> page = new PageImpl<>(List.of(agendaDTO));
        when(agendaService.getAll(0, 10)).thenReturn(page);

        mockMvc.perform(get("/agenda"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(agendaService, times(1)).getAll(0, 10);
    }

    @Test
    void shouldGetAllAgendas_withCustomParams_andReturnOk() throws Exception {
        AgendaDTO second = new AgendaDTO();
        second.setTitle("Segunda Pauta");
        Page<AgendaDTO> page = new PageImpl<>(List.of(agendaDTO, second));
        when(agendaService.getAll(1, 5)).thenReturn(page);

        mockMvc.perform(get("/agenda")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));

        verify(agendaService, times(1)).getAll(1, 5);
    }

    @Test
    void shouldGetAllAgendas_whenEmpty_andReturnOk() throws Exception {
        Page<AgendaDTO> emptyPage = new PageImpl<>(List.of());
        when(agendaService.getAll(anyInt(), anyInt())).thenReturn(emptyPage);

        mockMvc.perform(get("/agenda"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }


    @Test
    void shouldGetAgendaById_andReturnOk() throws Exception {
        when(agendaService.getById(1L)).thenReturn(agendaDTO);

        mockMvc.perform(get("/agenda/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Pauta Teste"));

        verify(agendaService, times(1)).getById(1L);
    }

    @Test
    void shouldReturnNotFound_whenAgendaByIdDoesNotExist() throws Exception {
        when(agendaService.getById(99L))
                .thenThrow(new EntityNotFoundException("Pauta não encontrada"));

        mockMvc.perform(get("/agenda/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(agendaService, times(1)).getById(99L);
    }

    @Test
    void shouldOpenAgenda_withoutParams_andReturnOk() throws Exception {
        when(agendaService.startAgenda(eq(1L), isNull(), isNull())).thenReturn(agendaDTO);

        mockMvc.perform(patch("/agenda/{id}/open", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Pauta Teste"));

        verify(agendaService, times(1)).startAgenda(eq(1L), isNull(), isNull());
    }

    @Test
    void shouldOpenAgenda_withStartAtAndEndAt_andReturnOk() throws Exception {
        LocalDateTime startAt = LocalDateTime.of(2026, 3, 18, 14, 30, 0);
        LocalDateTime endAt   = LocalDateTime.of(2026, 3, 18, 15, 30, 0);

        when(agendaService.startAgenda(eq(1L), eq(startAt), eq(endAt))).thenReturn(agendaDTO);

        mockMvc.perform(patch("/agenda/{id}/open", 1L)
                        .param("startAt", "18/03/2026 14:30:00")
                        .param("endAt",   "18/03/2026 15:30:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Pauta Teste"));

        verify(agendaService, times(1)).startAgenda(eq(1L), eq(startAt), eq(endAt));
    }

    @Test
    void shouldReturnNotFound_whenOpeningNonExistentAgenda() throws Exception {
        when(agendaService.startAgenda(eq(99L), any(), any()))
                .thenThrow(new EntityNotFoundException("Pauta não encontrada"));

        mockMvc.perform(patch("/agenda/{id}/open", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllOpenedAgendas_andReturnOk() throws Exception {
        Page<AgendaDTO> page = new PageImpl<>(List.of(agendaDTO));
        when(agendaService.getAllOpened(0, 10)).thenReturn(page);

        mockMvc.perform(get("/agenda/opened")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Pauta Teste"));

        verify(agendaService, times(1)).getAllOpened(0, 10);
    }

    @Test
    void shouldGetAllOpenedAgendas_withDefaultParams_andReturnOk() throws Exception {
        Page<AgendaDTO> page = new PageImpl<>(List.of());
        when(agendaService.getAllOpened(0, 10)).thenReturn(page);

        mockMvc.perform(get("/agenda/opened"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(agendaService, times(1)).getAllOpened(0, 10);
    }

    @Test
    void shouldGetAgendaResult_andReturnOk() throws Exception {
        AgendaResultDTO resultDTO = new AgendaResultDTO();
        when(agendaService.getResultById(1L)).thenReturn(resultDTO);

        mockMvc.perform(get("/agenda/{id}/result", 1L))
                .andExpect(status().isOk());

        verify(agendaService, times(1)).getResultById(1L);
    }

    @Test
    void shouldReturnNotFound_whenResultOfNonExistentAgenda() throws Exception {
        when(agendaService.getResultById(99L))
                .thenThrow(new EntityNotFoundException("Pauta não encontrada"));

        mockMvc.perform(get("/agenda/{id}/result", 99L))
                .andExpect(status().isNotFound());

        verify(agendaService, times(1)).getResultById(99L);
    }
}
