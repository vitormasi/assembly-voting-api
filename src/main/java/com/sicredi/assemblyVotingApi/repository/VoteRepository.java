package com.sicredi.assemblyVotingApi.repository;

import com.sicredi.assemblyVotingApi.entity.Vote;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByAgendaIdAndCpf(Long agendaId, String cpf);

}
