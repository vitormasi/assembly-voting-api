package com.sicredi.assemblyVotingApi.service.impl;

import com.sicredi.assemblyVotingApi.entity.Agenda;
import com.sicredi.assemblyVotingApi.entity.dto.AgendaDTO;
import com.sicredi.assemblyVotingApi.entity.dto.AgendaResultDTO;
import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;
import com.sicredi.assemblyVotingApi.mapper.AgendaMapper;
import com.sicredi.assemblyVotingApi.repository.AgendaRepository;
import com.sicredi.assemblyVotingApi.repository.VoteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendaServiceImplTest {

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private AgendaServiceImpl agendaService;

    @Test
    void create_ShouldSaveAndReturnDTO() {
        AgendaDTO dto = new AgendaDTO();
        dto.setTitle("Pauta Teste");

        Agenda entity = AgendaMapper.toEntity(dto);
        when(agendaRepository.save(any())).thenReturn(entity);

        AgendaDTO result = agendaService.create(dto);

        assertNotNull(result);
        verify(agendaRepository, times(1)).save(any());
    }

    @Test
    void create_WhenOnlyEndAtProvided_ShouldSetStartAtToNow() {
        AgendaDTO dto = new AgendaDTO();
        dto.setTitle("Pauta Teste");
        dto.setEndAt(LocalDateTime.now().plusMinutes(30));

        Agenda entity = AgendaMapper.toEntity(dto);
        when(agendaRepository.save(any())).thenReturn(entity);

        AgendaDTO result = agendaService.create(dto);

        assertNotNull(result);
        verify(agendaRepository, times(1)).save(any());
    }

    @Test
    void create_WhenOnlyStartAtProvided_ShouldSetEndAtToStartAtPlusOneMinute() {
        LocalDateTime startAt = LocalDateTime.now().plusMinutes(5);
        AgendaDTO dto = new AgendaDTO();
        dto.setTitle("Pauta Teste");
        dto.setStartAt(startAt);

        Agenda savedEntity = Agenda.builder()
                .id(1L)
                .title("Pauta Teste")
                .startAt(startAt)
                .endAt(startAt.plusMinutes(1))
                .build();

        when(agendaRepository.save(any())).thenReturn(savedEntity);

        AgendaDTO result = agendaService.create(dto);

        assertNotNull(result);
        assertEquals(startAt.plusMinutes(1), result.getEndAt());
    }

    @Test
    void create_WhenStartAtEqualsEndAt_ShouldThrowIllegalArgumentException() {
        LocalDateTime sameDate = LocalDateTime.now().plusMinutes(10);
        AgendaDTO dto = new AgendaDTO();
        dto.setTitle("Pauta Teste");
        dto.setStartAt(sameDate);
        dto.setEndAt(sameDate);

        assertThrows(IllegalArgumentException.class, () -> agendaService.create(dto));
        verify(agendaRepository, never()).save(any());
    }

    @Test
    void create_WhenBothDatesProvided_ShouldSaveNormally() {
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = startAt.plusMinutes(30);
        AgendaDTO dto = new AgendaDTO();
        dto.setTitle("Pauta Teste");
        dto.setStartAt(startAt);
        dto.setEndAt(endAt);

        Agenda entity = AgendaMapper.toEntity(dto);
        when(agendaRepository.save(any())).thenReturn(entity);

        AgendaDTO result = agendaService.create(dto);

        assertNotNull(result);
        verify(agendaRepository, times(1)).save(any());
    }

    @Test
    void getAll_ShouldReturnPageOfAgendaDTOs() {
        Agenda agenda = Agenda.builder().id(1L).title("Pauta").build();
        Page<Agenda> page = new PageImpl<>(List.of(agenda));

        when(agendaRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<AgendaDTO> result = agendaService.getAll(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(agendaRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAll_WhenEmpty_ShouldReturnEmptyPage() {
        Page<Agenda> emptyPage = new PageImpl<>(List.of());
        when(agendaRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<AgendaDTO> result = agendaService.getAll(0, 10);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getById_WhenExists_ShouldReturnDTO() {
        Agenda agenda = Agenda.builder().id(1L).title("Pauta").build();
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));

        AgendaDTO result = agendaService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Pauta", result.getTitle());
    }

    @Test
    void getById_WhenNotFound_ShouldThrowEntityNotFoundException() {
        when(agendaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> agendaService.getById(99L));
    }

    @Test
    void startAgenda_WhenNotStarted_ShouldUpdateAndReturn() {
        Agenda agenda = Agenda.builder().id(1L).title("Pauta").build();
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = startAt.plusMinutes(10);

        Agenda savedAgenda = Agenda.builder()
                .id(1L)
                .title("Pauta")
                .startAt(startAt)
                .endAt(endAt)
                .build();

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(agendaRepository.save(any())).thenReturn(savedAgenda);

        AgendaDTO result = agendaService.startAgenda(1L, startAt, endAt);

        assertNotNull(result);
        assertNotNull(result.getStartAt());
        assertNotNull(result.getEndAt());
        verify(agendaRepository, times(1)).save(any());
    }

    @Test
    void startAgenda_WhenAlreadyStarted_ShouldThrowIllegalStateException() {
        Agenda agenda = Agenda.builder()
                .id(1L)
                .title("Pauta")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));

        assertThrows(IllegalStateException.class,
                () -> agendaService.startAgenda(1L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5)));

        verify(agendaRepository, never()).save(any());
    }

    @Test
    void startAgenda_WhenNotFound_ShouldThrowEntityNotFoundException() {
        when(agendaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> agendaService.startAgenda(99L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5)));
    }

    @Test
    void startAgenda_WhenNoDatesProvided_ShouldSetDefaultDates() {
        Agenda agenda = Agenda.builder().id(1L).title("Pauta").build();

        Agenda savedAgenda = Agenda.builder()
                .id(1L)
                .title("Pauta")
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusMinutes(1))
                .build();

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(agendaRepository.save(any())).thenReturn(savedAgenda);

        AgendaDTO result = agendaService.startAgenda(1L, null, null);

        assertNotNull(result);
        assertNotNull(result.getStartAt());
        assertNotNull(result.getEndAt());
    }

    @Test
    void startAgenda_WhenStartAtEqualsEndAt_ShouldThrowIllegalArgumentException() {
        Agenda agenda = Agenda.builder().id(1L).title("Pauta").build();
        LocalDateTime sameDate = LocalDateTime.now().plusMinutes(5);

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));

        assertThrows(IllegalArgumentException.class,
                () -> agendaService.startAgenda(1L, sameDate, sameDate));

        verify(agendaRepository, never()).save(any());
    }

    @Test
    void getAllOpened_ShouldReturnOpenedAgendas() {
        Agenda agenda = Agenda.builder()
                .id(1L)
                .title("Pauta Aberta")
                .startAt(LocalDateTime.now().minusMinutes(5))
                .endAt(LocalDateTime.now().plusMinutes(5))
                .build();

        Page<Agenda> page = new PageImpl<>(List.of(agenda));

        when(agendaRepository.findAllByDateTimeBetweenStartAtAndEndAt(any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(page);

        Page<AgendaDTO> result = agendaService.getAllOpened(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllOpened_WhenNoOpenedAgendas_ShouldReturnEmptyPage() {
        Page<Agenda> emptyPage = new PageImpl<>(List.of());

        when(agendaRepository.findAllByDateTimeBetweenStartAtAndEndAt(any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        Page<AgendaDTO> result = agendaService.getAllOpened(0, 10);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getResultById_ShouldReturnResultWithVoteCounts() {
        Agenda agenda = Agenda.builder().id(1L).title("Pauta").build();

        List<Object[]> voteCountRows = List.of(
                new Object[]{VoteEnum.SIM, 10L},
                new Object[]{VoteEnum.NAO, 3L}
        );

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(voteRepository.countVotesByAgendaId(1L)).thenReturn(voteCountRows);

        AgendaResultDTO result = agendaService.getResultById(1L);

        assertNotNull(result);
        verify(voteRepository, times(1)).countVotesByAgendaId(1L);
    }

    @Test
    void getResultById_WhenNotFound_ShouldThrowEntityNotFoundException() {
        when(agendaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> agendaService.getResultById(99L));
        verify(voteRepository, never()).countVotesByAgendaId(any());
    }

    @Test
    void getResultById_WhenNoVotes_ShouldReturnResultWithEmptyCounts() {
        Agenda agenda = Agenda.builder().id(1L).title("Pauta").build();

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(voteRepository.countVotesByAgendaId(1L)).thenReturn(List.of());

        AgendaResultDTO result = agendaService.getResultById(1L);

        assertNotNull(result);}
}
