package com.sicredi.assemblyVotingApi.service;

import com.sicredi.assemblyVotingApi.client.CpfValidationClient;
import com.sicredi.assemblyVotingApi.client.dto.CpfValidationResponse;
import com.sicredi.assemblyVotingApi.client.enumeration.CpfStatusEnum;
import com.sicredi.assemblyVotingApi.entity.Agenda;
import com.sicredi.assemblyVotingApi.entity.Vote;
import com.sicredi.assemblyVotingApi.entity.dto.VoteDTO;
import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;
import com.sicredi.assemblyVotingApi.mapper.VoteMapper;
import com.sicredi.assemblyVotingApi.repository.VoteRepository;
import com.sicredi.assemblyVotingApi.utils.CpfUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class VoteServiceImpl implements VoteService {

    private final AgendaService agendaService;
    private final VoteRepository voteRepository;
    private final CpfValidationClient cpfValidationClient;

    @Override
    public VoteDTO createVote(Long agendaId, String cpf, VoteEnum voteEnum) throws Exception {
        Agenda agenda = checkAgenda(agendaId);
        cpf = CpfUtils.onlyNumbers(cpf);
        checkCpfAlreadyVote(agenda, cpf);

        Vote vote = voteRepository.save(VoteMapper.toEntity(agenda, cpf, voteEnum));

        return VoteMapper.toDTO(vote);
    }

    @Override
    public VoteDTO createVoteV2(Long agendaId, String cpf, VoteEnum voteEnum) throws Exception {
        Agenda agenda = checkAgenda(agendaId);
        cpf = CpfUtils.onlyNumbers(cpf);
        checkCpfCanVote(cpf);
        checkCpfAlreadyVote(agenda, cpf);

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

    private void checkCpfAlreadyVote(Agenda agenda, String cpf) {
        if (voteRepository.existsByAgendaIdAndCpf(agenda.getId(), cpf)) {
            throw new IllegalStateException("CPF já votou nessa pauta");
        }
    }

    private void checkCpfCanVote(String cpf) throws Exception {
        try {
            CpfValidationResponse response = cpfValidationClient.validateCpf(cpf);

            if (ObjectUtils.isEmpty(response) || ObjectUtils.isEmpty(response.getStatus())) {
                throw new Exception("Resposta inválida do serviço de validação de CPF");
            }

            if (!response.getStatus().equals(CpfStatusEnum.ABLE_TO_VOTE)) {
                throw new IllegalStateException("CPF não habilitado para votar");
            }
        } catch (Exception e) {
            log.error("Erro ao validar CPF: Serviço para validação de CPF indisponível", e.getMessage());
            throw new Exception("Erro ao validar CPF: " + "Serviço para validação de CPF indisponível");
        }
    }
}
