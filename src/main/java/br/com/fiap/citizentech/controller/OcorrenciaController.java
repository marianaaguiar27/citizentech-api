package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.assembler.OcorrenciaModelAssembler;
import br.com.fiap.citizentech.dto.*;
import br.com.fiap.citizentech.model.StatusOcorrencia;
import br.com.fiap.citizentech.model.TipoOcorrencia;
import br.com.fiap.citizentech.service.OcorrenciaService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import br.com.fiap.citizentech.security.UsuarioLogado;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ocorrencias")
@RequiredArgsConstructor
public class OcorrenciaController {

    private final OcorrenciaService service;
    private final OcorrenciaModelAssembler assembler;
    private final PagedResourcesAssembler<OcorrenciaResponse> pagedAssembler;

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

    @GetMapping("/estatisticas")
    public List<ContagemPorTipo> estatisticas(@RequestParam Long regiaoId) {
        return service.estatisticas(regiaoId);
    }

    @GetMapping("/{id}")
    public EntityModel<OcorrenciaResponse> buscar(@PathVariable Long id) {
        return assembler.toModel(service.buscar(id));
    }

    @PostMapping
    public ResponseEntity<EntityModel<OcorrenciaResponse>> criar(@RequestBody @Valid OcorrenciaRequest dados,
                                                                 @AuthenticationPrincipal UsuarioLogado usuario) {
        EntityModel<OcorrenciaResponse> criada = assembler.toModel(service.criar(dados, usuario.id()));
        return ResponseEntity.created(criada.getRequiredLink("self").toUri()).body(criada);
    }

    @PutMapping("/{id}")
    public EntityModel<OcorrenciaResponse> atualizar(@PathVariable Long id,
                                                     @RequestBody @Valid OcorrenciaRequest dados) {
        return assembler.toModel(service.atualizar(id, dados));
    }

    @PatchMapping("/{id}/status")
    public EntityModel<OcorrenciaResponse> atualizarStatus(@PathVariable Long id,
                                                           @RequestBody @Valid StatusRequest dados) {
        return assembler.toModel(service.atualizarStatus(id, dados.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}