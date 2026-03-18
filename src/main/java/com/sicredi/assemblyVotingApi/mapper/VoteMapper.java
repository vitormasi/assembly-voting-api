package com.sicredi.assemblyVotingApi.mapper;

import com.sicredi.assemblyVotingApi.entity.Agenda;
import com.sicredi.assemblyVotingApi.entity.Vote;
import com.sicredi.assemblyVotingApi.entity.dto.VoteDTO;
import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;

import java.time.LocalDateTime;

public class VoteMapper {

    public static Vote toEntity(Agenda agenda, String cpf, VoteEnum voteEnum) {
        return Vote.builder()
                .cpf(cpf)
                .agenda(agenda)
                .vote(voteEnum)
                .creationDate(LocalDateTime.now())
                .build();
    }

    public static VoteDTO toDTO(Vote vote) {
        if (vote == null) {
            return null;
        }

        return VoteDTO.builder()
                .cpf(vote.getCpf())
                .vote(vote.getVote())
                .creationDate(vote.getCreationDate())
                .agendaId(vote.getAgenda().getId())
                .build();
    }

}
