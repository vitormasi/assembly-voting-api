package com.sicredi.assemblyVotingApi.controller;

import com.sicredi.assemblyVotingApi.entity.dto.AgendaDTO;
import com.sicredi.assemblyVotingApi.entity.dto.AgendaResultDTO;
import com.sicredi.assemblyVotingApi.service.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/agenda")
@AllArgsConstructor
@Tag(name = "Agenda", description = "Operações relacionadas às pautas de votação")
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping
    @Operation(summary = "Criar pauta", description = "Cria uma nova pauta de votação")
    public ResponseEntity<AgendaDTO> create(
            @RequestBody @Valid AgendaDTO agendaDTO
    ) {
        return new ResponseEntity<>(agendaService.create(agendaDTO), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Buscar todas pautas", description = "Retorna uma lista com todas as pautas cadastradas")
    public ResponseEntity<Page<AgendaDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(agendaService.getAll(page, size), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pauta por ID", description = "Retorna uma pauta específica com base no ID informado")
    public ResponseEntity<AgendaDTO> getById(@PathVariable Long id) {
        return new ResponseEntity<>(agendaService.getById(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}/open")
    @Operation(summary = "Abrir pauta para votação", description = "Abre uma pauta para votação, definindo o horário de início e duração da votação")
    public ResponseEntity<AgendaDTO> open(
            @PathVariable Long id,
            @Parameter(description = "Data/hora de início da votação", schema = @Schema(type = "string", example = "18/03/2026 14:30:00"))
            @RequestParam(required = false) LocalDateTime startAt,
            @Parameter(description = "Data/hora de encerramento da votação", schema = @Schema(type = "string", example = "18/03/2026 14:30:00"))
            @RequestParam(required = false) LocalDateTime endAt
    ) {
        return new ResponseEntity<>(agendaService.startAgenda(id, startAt, endAt), HttpStatus.OK);
    }

    @GetMapping("/opened")
    @Operation(summary = "Buscar todas pautas abertar para votação", description = "Retorna uma lista com todas as pautas abertas para votação")
    public ResponseEntity<Page<AgendaDTO>> getAllOpened(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(agendaService.getAllOpened(page, size), HttpStatus.OK);
    }

    @GetMapping("/{id}/result")
    @Operation(summary = "Buscar resultado da votação", description = "Retorna o resultado da votação de uma pauta específica")
    public ResponseEntity<AgendaResultDTO> getResultById(@PathVariable Long id) {
        return new ResponseEntity<>(agendaService.getResultById(id), HttpStatus.OK);
    }

}
