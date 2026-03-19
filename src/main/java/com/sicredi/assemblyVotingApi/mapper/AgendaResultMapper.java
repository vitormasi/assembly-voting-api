package com.sicredi.assemblyVotingApi.mapper;

import com.sicredi.assemblyVotingApi.entity.Agenda;
import com.sicredi.assemblyVotingApi.entity.Vote;
import com.sicredi.assemblyVotingApi.entity.dto.AgendaResultDTO;
import com.sicredi.assemblyVotingApi.entity.enumeration.StatusEnum;
import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AgendaResultMapper {

    public static AgendaResultDTO toAgendaResultDTO(Agenda agenda) {
        if (agenda == null) {
            return null;
        }

        return AgendaResultDTO.builder()
                .id(agenda.getId())
                .title(agenda.getTitle())
                .startAt(agenda.getStartAt())
                .endAt(agenda.getEndAt())
                .status(defineStatus(agenda))
                .result(defineResult(agenda.getVotes()))
                .votesCount(defineVotesCount(agenda.getVotes()))
                .build();
    }

    private static StatusEnum defineStatus(Agenda agenda) {
        if (agenda.getStartAt() == null || agenda.getEndAt() == null || LocalDateTime.now().isBefore(agenda.getStartAt())) {
            return StatusEnum.NOT_STARTED;
        }

        if (LocalDateTime.now().isAfter(agenda.getStartAt()) && LocalDateTime.now().isBefore(agenda.getEndAt())) {
            return StatusEnum.IN_PROGRESS;
        }

        return StatusEnum.FINISHED;
    }

    private static VoteEnum defineResult(List<Vote> votes) {
        if (votes == null || votes.isEmpty()) return null;

        Map<VoteEnum, Long> counts = votes.stream()
                .map(Vote::getVote)
                .collect(Collectors.groupingBy(v -> v, Collectors.counting()));

        long maxCount = Collections.max(counts.values());

        boolean hasTie = counts.values().stream()
                .filter(c -> c.equals(maxCount))
                .count() > 1;

        return hasTie ? null : counts.entrySet().stream()
                .filter(e -> e.getValue().equals(maxCount))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private static Map<String, Long> defineVotesCount(List<Vote> votes) {
        if (votes == null || votes.isEmpty()) return Collections.emptyMap();

        return votes.stream()
                .collect(Collectors.groupingBy(vote -> vote.getVote().name(), Collectors.counting()));
    }


}
