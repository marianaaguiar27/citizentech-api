package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.dto.AlertaRequest;
import br.com.fiap.citizentech.dto.AlertaResponse;
import br.com.fiap.citizentech.model.StatusAlerta;
import br.com.fiap.citizentech.service.AlertaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springdoc.core.annotations.ParameterObject;


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService service;

    @GetMapping
    public Page<AlertaResponse> listarPorRegiao(@RequestParam Long regiaoId,
                                                @RequestParam(defaultValue = "ATIVO") StatusAlerta status,
                                                @ParameterObject @PageableDefault(size = 10, sort = "criadoEm",
                                                        direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listarPorRegiao(regiaoId, status, pageable);
    }

    @GetMapping("/ativos")
    public List<AlertaResponse> ativos() {
        return service.listarAtivos();
    }

    @GetMapping("/{id}")
    public AlertaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<AlertaResponse> criar(@RequestBody @Valid AlertaRequest dados) {
        AlertaResponse criado = service.criar(dados);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @PatchMapping("/{id}/resolver")
    public AlertaResponse resolver(@PathVariable Long id) {
        return service.resolver(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}