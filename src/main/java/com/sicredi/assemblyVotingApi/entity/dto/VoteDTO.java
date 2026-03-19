package com.sicredi.assemblyVotingApi.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoteDTO {

    @Schema(description = "Identificador único do voto", example = "1")
    private String cpf;

    @Schema(description = "Valor do voto (SIM ou NAO)", example = "NAO")
    private VoteEnum vote;

    @Schema(description = "Data do registro do voto", example = "01/01/2025 08:00:00")
    private LocalDateTime creationDate;

    @Schema(description = "Identificador da pauta associada ao voto", example = "1")
    private Long agendaId;

}
