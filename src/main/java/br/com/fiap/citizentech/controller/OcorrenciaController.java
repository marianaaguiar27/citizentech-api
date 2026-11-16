package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.dto.*;
import br.com.fiap.citizentech.model.StatusOcorrencia;
import br.com.fiap.citizentech.model.TipoOcorrencia;
import br.com.fiap.citizentech.service.OcorrenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springdoc.core.annotations.ParameterObject;


import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ocorrencias")
@RequiredArgsConstructor
public class OcorrenciaController {

    private final OcorrenciaService service;

    // Todos os filtros são opcionais e combináveis (Specification)
    @GetMapping
    public Page<OcorrenciaResponse> filtrar(
            @RequestParam(required = false) TipoOcorrencia tipo,
            @RequestParam(required = false) StatusOcorrencia status,
            @RequestParam(required = false) Long regiaoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @ParameterObject @PageableDefault(size = 10, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.filtrar(tipo, status, regiaoId, desde, pageable);
    }

    @GetMapping("/estatisticas")
    public List<ContagemPorTipo> estatisticas(@RequestParam Long regiaoId) {
        return service.estatisticas(regiaoId);
    }

    @GetMapping("/{id}")
    public OcorrenciaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<OcorrenciaResponse> criar(@RequestBody @Valid OcorrenciaRequest dados) {
        OcorrenciaResponse criada = service.criar(dados);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(local).body(criada);
    }

    @PutMapping("/{id}")
    public OcorrenciaResponse atualizar(@PathVariable Long id, @RequestBody @Valid OcorrenciaRequest dados) {
        return service.atualizar(id, dados);
    }

    @PatchMapping("/{id}/status")
    public OcorrenciaResponse atualizarStatus(@PathVariable Long id, @RequestBody @Valid StatusRequest dados) {
        return service.atualizarStatus(id, dados.status());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}