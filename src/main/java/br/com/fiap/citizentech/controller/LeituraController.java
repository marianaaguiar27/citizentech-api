package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.dto.LeituraRegistradaResponse;
import br.com.fiap.citizentech.dto.LeituraRequest;
import br.com.fiap.citizentech.dto.LeituraResponse;
import br.com.fiap.citizentech.service.LeituraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/leituras")
@RequiredArgsConstructor
public class LeituraController {

    private final LeituraService service;

    @GetMapping
    public Page<LeituraResponse> listar(@RequestParam(required = false) Long pontoId,
                                        @PageableDefault(size = 10, sort = "dataHora",
                                                direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listar(pontoId, pageable);
    }

    @GetMapping("/{id}")
    public LeituraResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/ponto/{pontoId}/ultima")
    public LeituraResponse ultima(@PathVariable Long pontoId) {
        return service.buscarUltima(pontoId);
    }

    @PostMapping
    public ResponseEntity<LeituraRegistradaResponse> registrar(@RequestBody @Valid LeituraRequest dados) {
        LeituraRegistradaResponse resposta = service.registrar(dados);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(resposta.leitura().id()).toUri();
        return ResponseEntity.created(local).body(resposta);
    }
}