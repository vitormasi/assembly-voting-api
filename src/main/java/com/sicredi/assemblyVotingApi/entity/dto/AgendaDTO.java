package com.sicredi.assemblyVotingApi.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class AgendaDTO {

    @Schema(description = "Identificador único da pauta", example = "1")
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Schema(description = "Título da pauta", example = "Aprovação do orçamento anual")
    private String title;

    @Schema(description = "Data/hora de início da votação", example = "01/01/2025 08:00:00")
    private LocalDateTime startAt;

    @Schema(description = "Data/hora de encerramento da votação", example = "01/01/2025 09:00:00")
    private LocalDateTime endAt;

}
