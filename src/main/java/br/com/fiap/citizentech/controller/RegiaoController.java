package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.dto.RegiaoRequest;
import br.com.fiap.citizentech.dto.RegiaoResponse;
import br.com.fiap.citizentech.model.NivelRisco;
import br.com.fiap.citizentech.service.RegiaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/regioes")
@RequiredArgsConstructor
public class RegiaoController {

    private final RegiaoService service;

    @GetMapping
    public Page<RegiaoResponse> listar(@RequestParam(required = false) NivelRisco nivelRisco,
                                       @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return service.listar(nivelRisco, pageable);
    }

    @GetMapping("/{id}")
    public RegiaoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<RegiaoResponse> criar(@RequestBody @Valid RegiaoRequest dados) {
        RegiaoResponse criada = service.criar(dados);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(local).body(criada); // 201 + header Location
    }

    @PutMapping("/{id}")
    public RegiaoResponse atualizar(@PathVariable Long id, @RequestBody @Valid RegiaoRequest dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build(); // 204
    }
}