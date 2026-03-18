package com.sicredi.assemblyVotingApi.controller;

import com.sicredi.assemblyVotingApi.entity.dto.AgendaDTO;
import com.sicredi.assemblyVotingApi.service.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agenda")
@AllArgsConstructor
@Tag(name = "Agenda", description = "Operações relacionadas às pautas de votação")
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping
    @Operation(summary = "Criar pauta", description = "Cria uma nova pauta de votação")
    public ResponseEntity<AgendaDTO> create(
            @RequestBody AgendaDTO agendaDTO
     ) {
        return new ResponseEntity<>(agendaService.create(agendaDTO), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Buscar todas pautas", description = "Retorna uma lista com todas as pautas cadastradas")
    public ResponseEntity<List<AgendaDTO>> getAll() {
        return new ResponseEntity<>(agendaService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pauta por ID", description = "Retorna uma pauta específica com base no ID informado")
    public ResponseEntity<AgendaDTO> getById(@PathVariable Long id) {
        return new ResponseEntity<>(agendaService.getById(id), HttpStatus.OK);
    }

}
