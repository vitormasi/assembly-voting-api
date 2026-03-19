package com.sicredi.assemblyVotingApi.service.impl;

import com.sicredi.assemblyVotingApi.entity.Agenda;
import com.sicredi.assemblyVotingApi.entity.dto.AgendaDTO;
import com.sicredi.assemblyVotingApi.entity.dto.AgendaResultDTO;
import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;
import com.sicredi.assemblyVotingApi.mapper.AgendaMapper;
import com.sicredi.assemblyVotingApi.mapper.AgendaResultMapper;
import com.sicredi.assemblyVotingApi.repository.AgendaRepository;
import com.sicredi.assemblyVotingApi.repository.VoteRepository;
import com.sicredi.assemblyVotingApi.service.AgendaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AgendaServiceImpl implements AgendaService {

    private final AgendaRepository agendaRepository;
    private final VoteRepository voteRepository;

    @Override
    public AgendaDTO create(AgendaDTO agendaDTO) {
        try {
            checkDates(agendaDTO);
            Agenda agenda = agendaRepository.save(AgendaMapper.toEntity(agendaDTO));
            return AgendaMapper.toDTO(agenda);
        } catch (IllegalArgumentException e) {
            log.warn("Datas inválidas ao criar pauta: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao criar pauta: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<AgendaDTO> getAll(int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return agendaRepository.findAll(pageable)
                .map(AgendaMapper::toDTO);
    }

    @Override
    public AgendaDTO getById(Long id) {
        return agendaRepository.findById(id)
                .map(AgendaMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Pauta não encontrada"));
    }

    @Override
    public AgendaDTO startAgenda(Long id, LocalDateTime startAt, LocalDateTime endAt) {
        try {
            Agenda agenda = agendaRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Pauta não encontrada"));

            if (ObjectUtils.isNotEmpty(agenda.getStartAt()) || ObjectUtils.isNotEmpty(agenda.getEndAt())) {
                throw new IllegalStateException("Pauta já foi aberta para votação");
            }

            AgendaDTO agendaDTO = AgendaMapper.toDTO(agenda);
            agendaDTO.setStartAt(startAt);
            agendaDTO.setEndAt(endAt);
            checkDates(agendaDTO);
            createDatesWhenNull(agendaDTO);

            Agenda updatedAgenda = agendaRepository.save(AgendaMapper.toEntity(agendaDTO));
            return AgendaMapper.toDTO(updatedAgenda);
        }  catch (EntityNotFoundException e) {
            log.warn("Pauta não encontrada ao abrir votação: id={}", id);
            throw e;
        } catch (IllegalStateException e) {
            log.warn("Tentativa de reabrir pauta já aberta: id={}", id);
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao abrir pauta: id={} | {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<AgendaDTO> getAllOpened(int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return agendaRepository.findAllByDateTimeBetweenStartAtAndEndAt(LocalDateTime.now(), pageable)
                .map(AgendaMapper::toDTO);
    }

    @Override
    public AgendaResultDTO getResultById(Long id) {
        try {
            Agenda agenda = agendaRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Pauta não encontrada"));

            Map<VoteEnum, Long> votesCounts = voteRepository.countVotesByAgendaId(id)
                    .stream()
                    .collect(Collectors.toMap(
                            row -> (VoteEnum) row[0],
                            row -> (Long) row[1]
                    ));

            return AgendaResultMapper.toAgendaResultDTO(agenda, votesCounts);
        } catch (EntityNotFoundException e) {
            log.warn("Pauta não encontrada ao buscar resultado: id={}", id);
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar resultado: id={} | {}", id, e.getMessage());
            throw e;
        }
    }

    private void checkDates(AgendaDTO agendaDTO) {
        if (ObjectUtils.isNotEmpty(agendaDTO.getStartAt()) && ObjectUtils.isNotEmpty(agendaDTO.getEndAt())) {
            if (agendaDTO.getStartAt().equals(agendaDTO.getEndAt())) {
                throw new IllegalArgumentException("Data de início deve ser anterior à data de fim");
            }
        }

        //Se receber data de fim, mas não receber data de início, a data de início será a data atual
        if (ObjectUtils.isNotEmpty(agendaDTO.getEndAt()) && ObjectUtils.isEmpty(agendaDTO.getStartAt())) {
            agendaDTO.setStartAt(LocalDateTime.now());
        }

        //Se receber data de início, mas não receber data de fim, a data de fim será a data de início + 1 minuto
        if (ObjectUtils.isNotEmpty(agendaDTO.getStartAt()) && ObjectUtils.isEmpty(agendaDTO.getEndAt())) {
            agendaDTO.setEndAt(agendaDTO.getStartAt().plusMinutes(1));
        }
    }

    private void createDatesWhenNull(AgendaDTO agendaDTO) {
        if (ObjectUtils.isEmpty(agendaDTO.getStartAt()) && ObjectUtils.isEmpty(agendaDTO.getEndAt())) {
            agendaDTO.setStartAt(LocalDateTime.now());
            agendaDTO.setEndAt(LocalDateTime.now().plusMinutes(1));
        }
    }
}
