package com.sicredi.assemblyVotingApi.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;
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
public class VoteDTO {

    private String cpf;

    private VoteEnum vote;

    @Schema(type = "string", example = "01/01/2025 08:00:00")
    private LocalDateTime creationDate;

    private Long agendaId;

}
