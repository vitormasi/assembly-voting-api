package com.sicredi.assemblyVotingApi.repository;

import com.sicredi.assemblyVotingApi.entity.Agenda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AgendaRepositoryTest {

    @Autowired
    private AgendaRepository agendaRepository;

    private Agenda openAgenda;
    private Agenda futureAgenda;
    private Agenda pastAgenda;

    @BeforeEach
    void setUp() {
        agendaRepository.deleteAll();

        openAgenda = new Agenda();
        openAgenda.setTitle("Pauta Aberta");
        openAgenda.setStartAt(LocalDateTime.now().minusMinutes(5));
        openAgenda.setEndAt(LocalDateTime.now().plusMinutes(5));

        futureAgenda = new Agenda();
        futureAgenda.setTitle("Pauta Futura");
        futureAgenda.setStartAt(LocalDateTime.now().plusHours(1));
        futureAgenda.setEndAt(LocalDateTime.now().plusHours(2));

        pastAgenda = new Agenda();
        pastAgenda.setTitle("Pauta Passada");
        pastAgenda.setStartAt(LocalDateTime.now().minusHours(2));
        pastAgenda.setEndAt(LocalDateTime.now().minusHours(1));
    }

    @Test
    void shouldFindOpenedAgendas() {
        agendaRepository.save(openAgenda);

        Page<Agenda> result = agendaRepository
                .findAllByDateTimeBetweenStartAtAndEndAt(LocalDateTime.now(), Pageable.ofSize(10));

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals("Pauta Aberta", result.getContent().get(0).getTitle());
    }

    @Test
    void shouldNotFindFutureAgenda() {
        agendaRepository.save(futureAgenda);

        Page<Agenda> result = agendaRepository
                .findAllByDateTimeBetweenStartAtAndEndAt(LocalDateTime.now(), Pageable.ofSize(10));

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldNotFindPastAgenda() {
        agendaRepository.save(pastAgenda);

        Page<Agenda> result = agendaRepository
                .findAllByDateTimeBetweenStartAtAndEndAt(LocalDateTime.now(), Pageable.ofSize(10));

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenNoAgendasExist() {
        Page<Agenda> result = agendaRepository
                .findAllByDateTimeBetweenStartAtAndEndAt(LocalDateTime.now(), Pageable.ofSize(10));

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void shouldReturnOnlyOpenAgendasWhenMixed() {
        agendaRepository.saveAll(List.of(openAgenda, futureAgenda, pastAgenda));

        Page<Agenda> result = agendaRepository
                .findAllByDateTimeBetweenStartAtAndEndAt(LocalDateTime.now(), Pageable.ofSize(10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Pauta Aberta", result.getContent().get(0).getTitle());
    }

    @Test
    void shouldReturnMultipleOpenAgendas() {
        Agenda anotherOpenAgenda = new Agenda();
        anotherOpenAgenda.setTitle("Outra Pauta Aberta");
        anotherOpenAgenda.setStartAt(LocalDateTime.now().minusMinutes(10));
        anotherOpenAgenda.setEndAt(LocalDateTime.now().plusMinutes(10));

        agendaRepository.saveAll(List.of(openAgenda, anotherOpenAgenda));

        Page<Agenda> result = agendaRepository
                .findAllByDateTimeBetweenStartAtAndEndAt(LocalDateTime.now(), Pageable.ofSize(10));

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void shouldRespectPaginationSize() {
        for (int i = 0; i < 5; i++) {
            Agenda agenda = new Agenda();
            agenda.setTitle("Pauta " + i);
            agenda.setStartAt(LocalDateTime.now().minusMinutes(5));
            agenda.setEndAt(LocalDateTime.now().plusMinutes(5));
            agendaRepository.save(agenda);
        }

        Page<Agenda> result = agendaRepository
                .findAllByDateTimeBetweenStartAtAndEndAt(LocalDateTime.now(), PageRequest.of(0, 2));

        assertEquals(2, result.getContent().size());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
    }

    @Test
    void shouldReturnSecondPage() {
        for (int i = 0; i < 4; i++) {
            Agenda agenda = new Agenda();
            agenda.setTitle("Pauta " + i);
            agenda.setStartAt(LocalDateTime.now().minusMinutes(5));
            agenda.setEndAt(LocalDateTime.now().plusMinutes(5));
            agendaRepository.save(agenda);
        }

        Page<Agenda> result = agendaRepository
                .findAllByDateTimeBetweenStartAtAndEndAt(LocalDateTime.now(), PageRequest.of(1, 2));

        assertEquals(2, result.getContent().size());
        assertEquals(4, result.getTotalElements());
    }

    @Test
    void shouldFindAgendaAtExactStartBoundary() {
        LocalDateTime startTime = LocalDateTime.now().minusSeconds(1);
        openAgenda.setStartAt(startTime);
        openAgenda.setEndAt(startTime.plusMinutes(10));
        agendaRepository.save(openAgenda);

        Page<Agenda> result = agendaRepository
                .findAllByDateTimeBetweenStartAtAndEndAt(LocalDateTime.now(), Pageable.ofSize(10));

        assertFalse(result.isEmpty());
    }

    @Test
    void shouldFindAgendaAtExactEndBoundary() {
        LocalDateTime endTime = LocalDateTime.now().plusSeconds(1);
        openAgenda.setStartAt(endTime.minusMinutes(10));
        openAgenda.setEndAt(endTime);
        agendaRepository.save(openAgenda);

        Page<Agenda> result = agendaRepository
                .findAllByDateTimeBetweenStartAtAndEndAt(LocalDateTime.now(), Pageable.ofSize(10));

        assertFalse(result.isEmpty());
    }

}
