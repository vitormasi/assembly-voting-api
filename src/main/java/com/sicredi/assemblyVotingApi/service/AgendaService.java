package com.sicredi.assemblyVotingApi.service;

import com.sicredi.assemblyVotingApi.entity.dto.AgendaDTO;

import java.util.List;

public interface AgendaService {

    AgendaDTO create(AgendaDTO agendaDTO);

    List<AgendaDTO> getAll();

    AgendaDTO getById(Long id);

}
