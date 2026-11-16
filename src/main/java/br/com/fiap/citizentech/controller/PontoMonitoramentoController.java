package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.assembler.PontoModelAssembler;
import br.com.fiap.citizentech.dto.PontoRequest;
import br.com.fiap.citizentech.dto.PontoResponse;
import br.com.fiap.citizentech.service.PontoMonitoramentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Pontos de monitoramento", description = "Sensores de nível de água instalados nas regiões")
@RestController
@RequestMapping("/api/pontos")
@RequiredArgsConstructor
public class PontoMonitoramentoController {

    private final PontoMonitoramentoService service;
    private final PontoModelAssembler assembler;
    private final PagedResourcesAssembler<PontoResponse> pagedAssembler;

    @Operation(summary = "Lista pontos (paginado), com filtro opcional por região")
    @GetMapping
    public PagedModel<EntityModel<PontoResponse>> listar(
            @RequestParam(required = false) Long regiaoId,
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return pagedAssembler.toModel(service.listar(regiaoId, pageable), assembler);
    }

    @Operation(summary = "Busca ponto por id")
    @GetMapping("/{id}")
    public EntityModel<PontoResponse> buscar(@PathVariable Long id) {
        return assembler.toModel(service.buscar(id));
    }

    @Operation(summary = "Cadastra ponto de monitoramento (admin)")
    @PostMapping
    public ResponseEntity<EntityModel<PontoResponse>> criar(@RequestBody @Valid PontoRequest dados) {
        EntityModel<PontoResponse> criado = assembler.toModel(service.criar(dados));
        return ResponseEntity.created(criado.getRequiredLink("self").toUri()).body(criado);
    }

    @Operation(summary = "Atualiza ponto de monitoramento (admin); inativar bloqueia novas leituras")
    @PutMapping("/{id}")
    public EntityModel<PontoResponse> atualizar(@PathVariable Long id, @RequestBody @Valid PontoRequest dados) {
        return assembler.toModel(service.atualizar(id, dados));
    }

    @Operation(summary = "Remove ponto de monitoramento (admin); 409 se houver leituras")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}