package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.dto.PontoRequest;
import br.com.fiap.citizentech.dto.PontoResponse;
import br.com.fiap.citizentech.service.PontoMonitoramentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springdoc.core.annotations.ParameterObject;


import java.net.URI;

@RestController
@RequestMapping("/api/pontos")
@RequiredArgsConstructor
public class PontoMonitoramentoController {

    private final PontoMonitoramentoService service;

    @GetMapping
    public Page<PontoResponse> listar(@RequestParam(required = false) Long regiaoId,
                                      @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return service.listar(regiaoId, pageable);
    }

    @GetMapping("/{id}")
    public PontoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<PontoResponse> criar(@RequestBody @Valid PontoRequest dados) {
        PontoResponse criado = service.criar(dados);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @PutMapping("/{id}")
    public PontoResponse atualizar(@PathVariable Long id, @RequestBody @Valid PontoRequest dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}