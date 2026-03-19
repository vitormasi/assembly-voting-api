package com.sicredi.assemblyVotingApi.controller;

import com.sicredi.assemblyVotingApi.entity.dto.VoteDTO;
import com.sicredi.assemblyVotingApi.entity.enumeration.VoteEnum;
import com.sicredi.assemblyVotingApi.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vote")
@AllArgsConstructor
@Tag(name = "Vote", description = "Operações relacionadas às votações de pautas")
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/agenda/{agendaId}")
    @Operation(summary = "Registrar Voto", description = "Cria um registro de voto para uma pauta específica")
    public ResponseEntity<VoteDTO> createVote(
            @PathVariable Long agendaId,
            @Schema(description = "CPF do eleitor", example = "12345678900")
            @RequestParam @NotBlank String cpf,
            @Schema(description = "Valor do voto (SIM ou NAO)", example = "NAO")
            @RequestParam VoteEnum voteEnum
    ) throws Exception {
        return new ResponseEntity<>(voteService.createVote(agendaId, cpf, voteEnum), HttpStatus.CREATED);
    }

}
