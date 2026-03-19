package com.sicredi.assemblyVotingApi.repository;

import com.sicredi.assemblyVotingApi.entity.Vote;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByAgendaIdAndCpf(Long agendaId, String cpf);

    @Query("SELECT v.vote, COUNT(v) FROM Vote v WHERE v.agenda.id = :agendaId GROUP BY v.vote")
    List<Object[]> countVotesByAgendaId(@Param("agendaId") Long agendaId);

}
