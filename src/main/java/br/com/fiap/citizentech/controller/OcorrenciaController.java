package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.assembler.OcorrenciaModelAssembler;
import br.com.fiap.citizentech.dto.*;
import br.com.fiap.citizentech.model.StatusOcorrencia;
import br.com.fiap.citizentech.model.TipoOcorrencia;
import br.com.fiap.citizentech.security.UsuarioLogado;
import br.com.fiap.citizentech.service.OcorrenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Ocorrências", description = "Zeladoria colaborativa: problemas urbanos reportados pelos moradores")
@RestController
@RequestMapping("/api/ocorrencias")
@RequiredArgsConstructor
public class OcorrenciaController {

    private final OcorrenciaService service;
    private final OcorrenciaModelAssembler assembler;
    private final PagedResourcesAssembler<OcorrenciaResponse> pagedAssembler;

    @Operation(summary = "Busca com filtros combináveis: tipo, status, região e período",
            description = "Todos os filtros são opcionais. O parâmetro 'desde' usa o formato 2026-11-16T10:00:00.")
    @GetMapping
    public PagedModel<EntityModel<OcorrenciaResponse>> filtrar(
            @RequestParam(required = false) TipoOcorrencia tipo,
            @RequestParam(required = false) StatusOcorrencia status,
            @RequestParam(required = false) Long regiaoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @ParameterObject @PageableDefault(size = 10, sort = "criadoEm",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return pagedAssembler.toModel(service.filtrar(tipo, status, regiaoId, desde, pageable), assembler);
    }

    @Operation(summary = "Contagem de ocorrências por tipo em uma região (cacheado)")
    @GetMapping("/estatisticas")
    public List<ContagemPorTipo> estatisticas(@RequestParam Long regiaoId) {
        return service.estatisticas(regiaoId);
    }

    @Operation(summary = "Busca ocorrência por id")
    @GetMapping("/{id}")
    public EntityModel<OcorrenciaResponse> buscar(@PathVariable Long id) {
        return assembler.toModel(service.buscar(id));
    }

    @Operation(summary = "Reporta uma ocorrência; o autor é o usuário do token")
    @PostMapping
    public ResponseEntity<EntityModel<OcorrenciaResponse>> criar(@RequestBody @Valid OcorrenciaRequest dados,
                                                                 @AuthenticationPrincipal UsuarioLogado usuario) {
        EntityModel<OcorrenciaResponse> criada = assembler.toModel(service.criar(dados, usuario.id()));
        return ResponseEntity.created(criada.getRequiredLink("self").toUri()).body(criada);
    }

    @Operation(summary = "Edita uma ocorrência não resolvida (defesa_civil/admin)")
    @PutMapping("/{id}")
    public EntityModel<OcorrenciaResponse> atualizar(@PathVariable Long id,
                                                     @RequestBody @Valid OcorrenciaRequest dados) {
        return assembler.toModel(service.atualizar(id, dados));
    }

    @Operation(summary = "Avança o status: aberta, em_analise, reslvida (defesa_civil/admin)",
            description = "O fluxo só avança. Voltar um status ou reabrir uma resolvida devolve 422.")
    @PatchMapping("/{id}/status")
    public EntityModel<OcorrenciaResponse> atualizarStatus(@PathVariable Long id,
                                                           @RequestBody @Valid StatusRequest dados) {
        return assembler.toModel(service.atualizarStatus(id, dados.status()));
    }

    @Operation(summary = "Remove ocorrência (admin)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}