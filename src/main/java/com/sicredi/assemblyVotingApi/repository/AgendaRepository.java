package com.sicredi.assemblyVotingApi.repository;

import com.sicredi.assemblyVotingApi.entity.Agenda;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface AgendaRepository extends JpaRepository<Agenda, Long> {

}
