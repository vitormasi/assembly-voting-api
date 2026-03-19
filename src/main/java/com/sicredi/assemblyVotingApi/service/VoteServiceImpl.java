package com.sicredi.assemblyVotingApi.service;

import com.sicredi.assemblyVotingApi.entity.Agenda;
import com.sicredi.assemblyVotingApi.entity.Vote;
import com.sicredi.assemblyVotingApi.entity.dto.VoteDTO;
import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;
import com.sicredi.assemblyVotingApi.mapper.VoteMapper;
import com.sicredi.assemblyVotingApi.repository.VoteRepository;
import com.sicredi.assemblyVotingApi.utils.CpfUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final AgendaService agendaService;
    private final VoteRepository voteRepository;

    @Override
    public VoteDTO createVote(Long agendaId, String cpf, VoteEnum voteEnum) throws Exception {
        Agenda agenda = checkAgenda(agendaId);
        cpf = CpfUtils.onlyNumbers(cpf);
        checkCpfCanVote(agenda, cpf);

        Vote vote = voteRepository.save(VoteMapper.toEntity(agenda, cpf, voteEnum));

        return VoteMapper.toDTO(vote);
    }

    private Agenda checkAgenda(Long agendaId) {
        Agenda agenda = agendaService.findById(agendaId);

        if (ObjectUtils.isNotEmpty(agenda) && ObjectUtils.isNotEmpty(agenda.getStartAt()) && ObjectUtils.isNotEmpty(agenda.getEndAt())) {
            if (LocalDateTime.now().isBefore(agenda.getStartAt()) || LocalDateTime.now().isAfter(agenda.getEndAt())) {
                throw new IllegalStateException("Pauta não está aberta para votação");
            }
        } else {
            throw new IllegalStateException("Pauta não está aberta para votação");
        }

        return agenda;
    }

    private void checkCpfCanVote(Agenda agenda, String cpf) {
        if (voteRepository.existsByAgendaIdAndCpf(agenda.getId(), cpf)) {
            throw new IllegalStateException("CPF já votou nessa pauta");
        }
    }
}
