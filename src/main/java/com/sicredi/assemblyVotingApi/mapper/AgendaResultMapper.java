package com.sicredi.assemblyVotingApi.mapper;

import com.sicredi.assemblyVotingApi.entity.Agenda;
import com.sicredi.assemblyVotingApi.entity.dto.AgendaResultDTO;
import com.sicredi.assemblyVotingApi.entity.enumeration.StatusEnum;
import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class AgendaResultMapper {

    public static AgendaResultDTO toAgendaResultDTO(Agenda agenda, Map<VoteEnum, Long> votesCounts) {
        if (agenda == null) {
            return null;
        }

        return AgendaResultDTO.builder()
                .id(agenda.getId())
                .title(agenda.getTitle())
                .startAt(agenda.getStartAt())
                .endAt(agenda.getEndAt())
                .status(defineStatus(agenda))
                .result(defineResult(votesCounts))
                .votesCount(defineVotesCount(votesCounts))
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

    private static VoteEnum defineResult(Map<VoteEnum, Long> counts) {
        if (counts == null || counts.isEmpty()) return null;

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

    private static Map<String, Long> defineVotesCount(Map<VoteEnum, Long> counts) {
        if (counts == null || counts.isEmpty()) return Collections.emptyMap();

        return counts.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().name(),
                        Map.Entry::getValue
                ));
    }
}
