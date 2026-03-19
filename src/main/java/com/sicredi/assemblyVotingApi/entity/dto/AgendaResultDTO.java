package com.sicredi.assemblyVotingApi.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sicredi.assemblyVotingApi.entity.enumeration.StatusEnum;
import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgendaResultDTO {

    @Schema(description = "Identificador único da pauta", example = "1")
    private Long id;

    @Schema(description = "Título da pauta", example = "Aprovação do orçamento anual")
    private String title;

    @Schema(description = "Data/hora de início da votação da pauta", example = "01/01/2025 08:00:00")
    private LocalDateTime startAt;

    @Schema(description = "Data/hora de fim da votação da pauta", example = "01/01/2025 08:00:00")
    private LocalDateTime endAt;

    @Schema(description = "Status atual da pauta", example = "IN_PROGRESS")
    private StatusEnum status;

    @Schema(description = "Resultado da votação da pauta (maioria dos votos)", example = "SIM")
    private VoteEnum result;

    @Schema(description = "Contagem de votos por opção (SIM, NAO)", example = "{\"SIM\": 10, \"NAO\": 5}")
    private Map<String, Long> votesCount;

}
