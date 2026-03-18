package com.sicredi.assemblyVotingApi.mapper;

import com.sicredi.assemblyVotingApi.entity.Agenda;
import com.sicredi.assemblyVotingApi.entity.dto.AgendaDTO;

import java.util.List;

public class AgendaMapper {

    public static Agenda toEntity(AgendaDTO agendaDTO) {
        if (agendaDTO == null) {
            return null;
        }

        return Agenda.builder()
                .id(agendaDTO.getId())
                .title(agendaDTO.getTitle())
                .startAt(agendaDTO.getStartAt())
                .endAt(agendaDTO.getEndAt())
                .build();
    }

    public static AgendaDTO toDTO(Agenda agenda) {
        if (agenda == null) {
            return null;
        }

        return AgendaDTO.builder()
                .id(agenda.getId())
                .title(agenda.getTitle())
                .startAt(agenda.getStartAt())
                .endAt(agenda.getEndAt())
                .build();
    }

    public static List<AgendaDTO> toDTOList (List<Agenda> agendaList) {
        if (agendaList == null) {
            return null;
        }

        return agendaList.stream()
                .map(AgendaMapper::toDTO)
                .toList();
    }

}
