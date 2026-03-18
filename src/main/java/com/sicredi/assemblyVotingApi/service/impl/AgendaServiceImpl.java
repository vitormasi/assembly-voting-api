package com.sicredi.assemblyVotingApi.service.impl;

import com.sicredi.assemblyVotingApi.entity.Agenda;
import com.sicredi.assemblyVotingApi.entity.dto.AgendaDTO;
import com.sicredi.assemblyVotingApi.mapper.AgendaMapper;
import com.sicredi.assemblyVotingApi.repository.AgendaRepository;
import com.sicredi.assemblyVotingApi.service.AgendaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AgendaServiceImpl implements AgendaService {

    private final AgendaRepository agendaRepository;

    @Override
    public AgendaDTO create(AgendaDTO agendaDTO) {
        Agenda agenda = agendaRepository.save(AgendaMapper.toEntity(agendaDTO));
        return AgendaMapper.toDTO(agenda);
    }

    @Override
    public List<AgendaDTO> getAll() {
        List<Agenda> agendaList = agendaRepository.findAll();
        return AgendaMapper.toDTOList(agendaList);
    }

    @Override
    public AgendaDTO getById(Long id) {
        return agendaRepository.findById(id)
                .map(AgendaMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Pauta não encontrada"));
    }
}
