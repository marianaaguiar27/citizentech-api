package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.assembler.LeituraModelAssembler;
import br.com.fiap.citizentech.dto.LeituraRegistradaResponse;
import br.com.fiap.citizentech.dto.LeituraRequest;
import br.com.fiap.citizentech.dto.LeituraResponse;
import br.com.fiap.citizentech.service.LeituraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Leituras", description = "Medições dos sensores. São imutáveis: não tem PUT nem DELETE")
@RestController
@RequestMapping("/api/leituras")
@RequiredArgsConstructor
public class LeituraController {

    private final LeituraService service;
    private final LeituraModelAssembler assembler;
    private final PagedResourcesAssembler<LeituraResponse> pagedAssembler;

    @Operation(summary = "Histórico de leituras (paginado), com filtro opcional por ponto")
    @GetMapping
    public PagedModel<EntityModel<LeituraResponse>> listar(
            @RequestParam(required = false) Long pontoId,
            @ParameterObject @PageableDefault(size = 10, sort = "dataHora",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return pagedAssembler.toModel(service.listar(pontoId, pageable), assembler);
    }

    @Operation(summary = "Busca leitura por id")
    @GetMapping("/{id}")
    public EntityModel<LeituraResponse> buscar(@PathVariable Long id) {
        return assembler.toModel(service.buscar(id));
    }

    @Operation(summary = "Última leitura de um ponto")
    @GetMapping("/ponto/{pontoId}/ultima")
    public EntityModel<LeituraResponse> ultima(@PathVariable Long pontoId) {
        return assembler.toModel(service.buscarUltima(pontoId));
    }

    @Operation(summary = "Registra leitura, recalcula o risco da região e gera alerta se necessário (defesa_civil/admin)",
            description = "Se dataHora for omitida, usa o horário atual. Ponto inativo devolve 422.")
    @PostMapping
    public ResponseEntity<LeituraRegistradaResponse> registrar(@RequestBody @Valid LeituraRequest dados) {
        LeituraRegistradaResponse resposta = service.registrar(dados);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(resposta.leitura().id()).toUri();
        return ResponseEntity.created(local).body(resposta);
    }
}