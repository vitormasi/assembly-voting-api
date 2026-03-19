package com.sicredi.assemblyVotingApi.repository;

import com.sicredi.assemblyVotingApi.entity.Agenda;
import com.sicredi.assemblyVotingApi.entity.Vote;
import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VoteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VoteRepository voteRepository;

    private Agenda agenda;

    @BeforeEach
    void setUp() {
        agenda = new Agenda();
        agenda.setTitle("Pauta de Teste");
        agenda.setStartAt(LocalDateTime.now());
        agenda.setEndAt(LocalDateTime.now().plusHours(1));
        entityManager.persist(agenda);

        Vote voteYes = new Vote();
        voteYes.setAgenda(agenda);
        voteYes.setVote(VoteEnum.SIM);
        voteYes.setCpf("12345678900");
        entityManager.persist(voteYes);

        Vote voteNo = new Vote();
        voteNo.setAgenda(agenda);
        voteNo.setVote(VoteEnum.NAO);
        voteNo.setCpf("98765432100");
        entityManager.persist(voteNo);

        entityManager.flush();
    }

    @Test
    @DisplayName("Deve retornar true quando CPF já votou na pauta")
    void shouldReturnTrueWhenCpfAlreadyVotedInAgenda() {
        boolean exists = voteRepository.existsByAgendaIdAndCpf(
                agenda.getId(), "12345678900"
        );

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando CPF não votou na pauta")
    void shouldReturnFalseWhenCpfDidNotVoteInAgenda() {
        boolean exists = voteRepository.existsByAgendaIdAndCpf(
                agenda.getId(), "00000000000"
        );

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false quando pauta não existe")
    void shouldReturnFalseWhenAgendaDoesNotExist() {
        boolean exists = voteRepository.existsByAgendaIdAndCpf(
                999L, "12345678900"
        );

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve retornar contagem agrupada por tipo de voto")
    void shouldReturnVoteCountGroupedByVoteType() {
        List<Object[]> result = voteRepository.countVotesByAgendaId(agenda.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Deve conter voto SIM com contagem correta")
    void shouldContainYesVoteWithCorrectCount() {
        List<Object[]> result = voteRepository.countVotesByAgendaId(agenda.getId());

        Object[] yesRow = result.stream()
                .filter(row -> VoteEnum.SIM.equals(row[0]))
                .findFirst()
                .orElseThrow();

        assertThat((Long) yesRow[1]).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve conter voto NÃO com contagem correta")
    void shouldContainNoVoteWithCorrectCount() {
        List<Object[]> result = voteRepository.countVotesByAgendaId(agenda.getId());

        Object[] noRow = result.stream()
                .filter(row -> VoteEnum.NAO.equals(row[0]))
                .findFirst()
                .orElseThrow();

        assertThat((Long) noRow[1]).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando pauta não tem votos")
    void shouldReturnEmptyListWhenAgendaHasNoVotes() {
        Agenda emptyAgenda = new Agenda();
        emptyAgenda.setTitle("Pauta Vazia");
        emptyAgenda.setStartAt(LocalDateTime.now());
        emptyAgenda.setEndAt(LocalDateTime.now().plusHours(1));
        entityManager.persist(emptyAgenda);
        entityManager.flush();

        List<Object[]> result = voteRepository.countVotesByAgendaId(emptyAgenda.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar contagem correta com múltiplos votos SIM")
    void shouldReturnCorrectCountWithMultipleYesVotes() {
        Vote extraVoteYes = new Vote();
        extraVoteYes.setAgenda(agenda);
        extraVoteYes.setVote(VoteEnum.SIM);
        extraVoteYes.setCpf("11122233344");
        entityManager.persist(extraVoteYes);
        entityManager.flush();

        List<Object[]> result = voteRepository.countVotesByAgendaId(agenda.getId());

        Object[] yesRow = result.stream()
                .filter(row -> VoteEnum.SIM.equals(row[0]))
                .findFirst()
                .orElseThrow();

        assertThat((Long) yesRow[1]).isEqualTo(2L);
    }
}
