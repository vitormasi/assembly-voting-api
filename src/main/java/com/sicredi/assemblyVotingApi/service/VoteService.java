package com.sicredi.assemblyVotingApi.service;

import com.sicredi.assemblyVotingApi.entity.dto.VoteDTO;
import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;

public interface VoteService {

    VoteDTO createVote(Long agendaId, String cpf, VoteEnum voteEnum);

}
