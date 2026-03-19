package com.sicredi.assemblyVotingApi.service;

import com.sicredi.assemblyVotingApi.entity.Agenda;
import com.sicredi.assemblyVotingApi.entity.dto.AgendaDTO;
import com.sicredi.assemblyVotingApi.entity.dto.AgendaResultDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface AgendaService {

    AgendaDTO create(AgendaDTO agendaDTO);

    Page<AgendaDTO> getAll(int page, int size);

    AgendaDTO getById(Long id);

    AgendaDTO startAgenda(Long id, LocalDateTime startAt, LocalDateTime endAt);

    Page<AgendaDTO> getAllOpened(int page, int size);

    Agenda findById(Long id);

    AgendaResultDTO getResultById(Long id);

}
