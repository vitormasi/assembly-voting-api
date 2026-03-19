package com.sicredi.assemblyVotingApi.service.impl;

import com.sicredi.assemblyVotingApi.client.CpfValidationClient;
import com.sicredi.assemblyVotingApi.client.dto.CpfValidationResponse;
import com.sicredi.assemblyVotingApi.client.enumeration.CpfStatusEnum;
import com.sicredi.assemblyVotingApi.entity.Agenda;
import com.sicredi.assemblyVotingApi.entity.Vote;
import com.sicredi.assemblyVotingApi.entity.dto.VoteDTO;
import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;
import com.sicredi.assemblyVotingApi.repository.AgendaRepository;
import com.sicredi.assemblyVotingApi.repository.VoteRepository;
import com.sicredi.assemblyVotingApi.service.VoteServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceImplTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private CpfValidationClient cpfValidationClient;

    @InjectMocks
    private VoteServiceImpl voteService;

    private Agenda buildOpenAgenda() {
        return Agenda.builder()
                .id(1L)
                .title("Pauta Teste")
                .startAt(LocalDateTime.now().minusMinutes(5))
                .endAt(LocalDateTime.now().plusMinutes(5))
                .build();
    }

    @Test
    void createVote_WhenValidInput_ShouldReturnVoteDTO() throws Exception {
        Agenda agenda = buildOpenAgenda();
        Vote vote = Vote.builder()
                .cpf("12345678901")
                .agenda(agenda)
                .vote(VoteEnum.SIM)
                .creationDate(LocalDateTime.now())
                .build();

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(voteRepository.existsByAgendaIdAndCpf(1L, "12345678901")).thenReturn(false);
        when(voteRepository.save(any())).thenReturn(vote);

        VoteDTO result = voteService.createVote(1L, "12345678901", VoteEnum.SIM);

        assertNotNull(result);
        assertEquals("12345678901", result.getCpf());
        assertEquals(VoteEnum.SIM, result.getVote());
        verify(voteRepository, times(1)).save(any());
    }

    @Test
    void createVote_WhenCpfWithMask_ShouldSanitizeAndVote() throws Exception {
        Agenda agenda = buildOpenAgenda();
        Vote vote = Vote.builder()
                .cpf("12345678901")
                .agenda(agenda)
                .vote(VoteEnum.NAO)
                .creationDate(LocalDateTime.now())
                .build();

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(voteRepository.existsByAgendaIdAndCpf(1L, "12345678901")).thenReturn(false);
        when(voteRepository.save(any())).thenReturn(vote);

        VoteDTO result = voteService.createVote(1L, "123.456.789-01", VoteEnum.NAO);

        assertNotNull(result);
        assertEquals("12345678901", result.getCpf());
    }

    @Test
    void createVote_WhenAgendaNotFound_ShouldThrowEntityNotFoundException() {
        when(agendaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> voteService.createVote(99L, "12345678901", VoteEnum.SIM));

        verify(voteRepository, never()).save(any());
    }

    @Test
    void createVote_WhenAgendaNotStarted_ShouldThrowIllegalStateException() {
        Agenda agenda = Agenda.builder()
                .id(1L)
                .title("Pauta Futura")
                .startAt(LocalDateTime.now().plusMinutes(10))
                .endAt(LocalDateTime.now().plusMinutes(20))
                .build();

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));

        assertThrows(IllegalStateException.class,
                () -> voteService.createVote(1L, "12345678901", VoteEnum.SIM));

        verify(voteRepository, never()).save(any());
    }

    @Test
    void createVote_WhenAgendaAlreadyClosed_ShouldThrowIllegalStateException() {
        Agenda agenda = Agenda.builder()
                .id(1L)
                .title("Pauta Encerrada")
                .startAt(LocalDateTime.now().minusMinutes(20))
                .endAt(LocalDateTime.now().minusMinutes(5))
                .build();

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));

        assertThrows(IllegalStateException.class,
                () -> voteService.createVote(1L, "12345678901", VoteEnum.SIM));

        verify(voteRepository, never()).save(any());
    }

    @Test
    void createVote_WhenAgendaHasNoDates_ShouldThrowIllegalStateException() {
        Agenda agenda = Agenda.builder()
                .id(1L)
                .title("Pauta Sem Datas")
                .build();

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));

        assertThrows(IllegalStateException.class,
                () -> voteService.createVote(1L, "12345678901", VoteEnum.SIM));

        verify(voteRepository, never()).save(any());
    }

    @Test
    void createVote_WhenCpfAlreadyVoted_ShouldThrowIllegalStateException() {
        Agenda agenda = buildOpenAgenda();

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(voteRepository.existsByAgendaIdAndCpf(1L, "12345678901")).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> voteService.createVote(1L, "12345678901", VoteEnum.SIM));

        verify(voteRepository, never()).save(any());
    }

    @Test
    void createVoteV2_WhenValidAndAbleToVote_ShouldReturnVoteDTO() throws Exception {
        Agenda agenda = buildOpenAgenda();
        Vote vote = Vote.builder()
                .cpf("12345678901")
                .agenda(agenda)
                .vote(VoteEnum.SIM)
                .creationDate(LocalDateTime.now())
                .build();

        CpfValidationResponse response = new CpfValidationResponse();
        response.setStatus(CpfStatusEnum.ABLE_TO_VOTE);

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(cpfValidationClient.validateCpf("12345678901")).thenReturn(response);
        when(voteRepository.existsByAgendaIdAndCpf(1L, "12345678901")).thenReturn(false);
        when(voteRepository.save(any())).thenReturn(vote);

        VoteDTO result = voteService.createVoteV2(1L, "12345678901", VoteEnum.SIM);

        assertNotNull(result);
        assertEquals(VoteEnum.SIM, result.getVote());
        verify(voteRepository, times(1)).save(any());
    }

    @Test
    void createVoteV2_WhenCpfUnableToVote_ShouldThrowException() throws Exception {
        Agenda agenda = buildOpenAgenda();

        CpfValidationResponse response = new CpfValidationResponse();
        response.setStatus(CpfStatusEnum.UNABLE_TO_VOTE);

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(cpfValidationClient.validateCpf("12345678901")).thenReturn(response);

        assertThrows(Exception.class,
                () -> voteService.createVoteV2(1L, "12345678901", VoteEnum.SIM));

        verify(voteRepository, never()).save(any());
    }

    @Test
    void createVoteV2_WhenCpfValidationResponseIsNull_ShouldThrowException() throws Exception {
        Agenda agenda = buildOpenAgenda();

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(cpfValidationClient.validateCpf("12345678901")).thenReturn(null);

        assertThrows(Exception.class,
                () -> voteService.createVoteV2(1L, "12345678901", VoteEnum.SIM));

        verify(voteRepository, never()).save(any());
    }

    @Test
    void createVoteV2_WhenCpfValidationServiceThrows_ShouldThrowException() throws Exception {
        Agenda agenda = buildOpenAgenda();

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(cpfValidationClient.validateCpf(any())).thenThrow(new RuntimeException("Serviço indisponível"));

        assertThrows(Exception.class,
                () -> voteService.createVoteV2(1L, "12345678901", VoteEnum.SIM));

        verify(voteRepository, never()).save(any());
    }

    @Test
    void createVoteV2_WhenAgendaNotFound_ShouldThrowEntityNotFoundException() {
        when(agendaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> voteService.createVoteV2(99L, "12345678901", VoteEnum.SIM));

        verify(cpfValidationClient, never()).validateCpf(any());
        verify(voteRepository, never()).save(any());
    }

    @Test
    void createVoteV2_WhenCpfAlreadyVoted_ShouldThrowIllegalStateException() throws Exception {
        Agenda agenda = buildOpenAgenda();

        CpfValidationResponse response = new CpfValidationResponse();
        response.setStatus(CpfStatusEnum.ABLE_TO_VOTE);

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(cpfValidationClient.validateCpf("12345678901")).thenReturn(response);
        when(voteRepository.existsByAgendaIdAndCpf(1L, "12345678901")).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> voteService.createVoteV2(1L, "12345678901", VoteEnum.SIM));

        verify(voteRepository, never()).save(any());
    }

    @Test
    void createVoteV2_WhenCpfWithMask_ShouldSanitizeAndVote() throws Exception {
        Agenda agenda = buildOpenAgenda();
        Vote vote = Vote.builder()
                .cpf("12345678901")
                .agenda(agenda)
                .vote(VoteEnum.SIM)
                .creationDate(LocalDateTime.now())
                .build();

        CpfValidationResponse response = new CpfValidationResponse();
        response.setStatus(CpfStatusEnum.ABLE_TO_VOTE);

        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(cpfValidationClient.validateCpf("12345678901")).thenReturn(response);
        when(voteRepository.existsByAgendaIdAndCpf(1L, "12345678901")).thenReturn(false);
        when(voteRepository.save(any())).thenReturn(vote);

        VoteDTO result = voteService.createVoteV2(1L, "123.456.789-01", VoteEnum.SIM);

        assertNotNull(result);
        assertEquals("12345678901", result.getCpf());
    }
}
