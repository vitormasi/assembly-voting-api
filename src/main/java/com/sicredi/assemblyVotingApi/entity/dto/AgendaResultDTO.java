package com.sicredi.assemblyVotingApi.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sicredi.assemblyVotingApi.entity.enumeration.StatusEnum;
import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgendaResultDTO {

    private Long id;

    private String title;

    @Schema(type = "string", example = "01/01/2025 08:00:00")
    private LocalDateTime startAt;

    @Schema(type = "string", example = "01/01/2025 08:00:00")
    private LocalDateTime endAt;

    private StatusEnum status;

    private VoteEnum result;

    private Map<String, Long> votesCount;

}
