package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.assembler.AlertaModelAssembler;
import br.com.fiap.citizentech.dto.AlertaRequest;
import br.com.fiap.citizentech.dto.AlertaResponse;
import br.com.fiap.citizentech.model.StatusAlerta;
import br.com.fiap.citizentech.service.AlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Tag(name = "Alertas", description = "Alertas de risco, automáticos (gerados por leituras) ou manuais")
@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService service;
    private final AlertaModelAssembler assembler;
    private final PagedResourcesAssembler<AlertaResponse> pagedAssembler;

    @Operation(summary = "Alertas de uma região (paginado), filtrados por status")
    @GetMapping
    public PagedModel<EntityModel<AlertaResponse>> listarPorRegiao(
            @RequestParam Long regiaoId,
            @RequestParam(defaultValue = "ATIVO") StatusAlerta status,
            @ParameterObject @PageableDefault(size = 10, sort = "criadoEm",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return pagedAssembler.toModel(service.listarPorRegiao(regiaoId, status, pageable), assembler);
    }

    @Operation(summary = "Todos os alertas ativos (cacheado)")
    @GetMapping("/ativos")
    public CollectionModel<EntityModel<AlertaResponse>> ativos() {
        return assembler.toCollectionModel(service.listarAtivos())
                .add(linkTo(AlertaController.class).slash("ativos").withSelfRel());
    }

    @Operation(summary = "Busca alerta por id")
    @GetMapping("/{id}")
    public EntityModel<AlertaResponse> buscar(@PathVariable Long id) {
        return assembler.toModel(service.buscar(id));
    }

    @Operation(summary = "Emite alerta manual (defesa_civil/admin)")
    @PostMapping
    public ResponseEntity<EntityModel<AlertaResponse>> criar(@RequestBody @Valid AlertaRequest dados) {
        EntityModel<AlertaResponse> criado = assembler.toModel(service.criar(dados));
        return ResponseEntity.created(criado.getRequiredLink("self").toUri()).body(criado);
    }

    @Operation(summary = "Marca o alerta como resolvido (defesa_civil/admin); 409 se já estiver resolvido")
    @PatchMapping("/{id}/resolver")
    public EntityModel<AlertaResponse> resolver(@PathVariable Long id) {
        return assembler.toModel(service.resolver(id));
    }

    @Operation(summary = "Remove alerta (admin)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}