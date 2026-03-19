package com.sicredi.assemblyVotingApi.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgendaDTO {

    private Long id;

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @Schema(type = "string", example = "01/01/2025 08:00:00")
    private LocalDateTime startAt;

    @Schema(type = "string", example = "01/01/2025 08:00:00")
    private LocalDateTime endAt;

}
