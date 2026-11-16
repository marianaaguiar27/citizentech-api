package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.assembler.RegiaoModelAssembler;
import br.com.fiap.citizentech.dto.RegiaoRequest;
import br.com.fiap.citizentech.dto.RegiaoResponse;
import br.com.fiap.citizentech.dto.RiscoRegiaoResponse;
import br.com.fiap.citizentech.model.NivelRisco;
import br.com.fiap.citizentech.service.RegiaoService;
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

@Tag(name = "Regiões", description = "Bairros e áreas monitoradas")
@RestController
@RequestMapping("/api/regioes")
@RequiredArgsConstructor
public class RegiaoController {

    private final RegiaoService service;
    private final RegiaoModelAssembler assembler;
    private final PagedResourcesAssembler<RegiaoResponse> pagedAssembler;

    @Operation(summary = "Lista regiões (paginado), com filtro opcional por nível de risco")
    @GetMapping
    public PagedModel<EntityModel<RegiaoResponse>> listar(
            @RequestParam(required = false) NivelRisco nivelRisco,
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return pagedAssembler.toModel(service.listar(nivelRisco, pageable), assembler);
    }

    @Operation(summary = "Busca região por id (cacheado)")
    @GetMapping("/{id}")
    public EntityModel<RegiaoResponse> buscar(@PathVariable Long id) {
        return assembler.toModel(service.buscar(id));
    }

    @Operation(summary = "Risco atual da região: pico de água nas últimas 6 horas (cacheado)")
    @GetMapping("/{id}/risco")
    public RiscoRegiaoResponse risco(@PathVariable Long id) {
        return service.consultarRisco(id);
    }

    @Operation(summary = "Cadastra região (admin)")
    @PostMapping
    public ResponseEntity<EntityModel<RegiaoResponse>> criar(@RequestBody @Valid RegiaoRequest dados) {
        EntityModel<RegiaoResponse> criada = assembler.toModel(service.criar(dados));
        return ResponseEntity.created(criada.getRequiredLink("self").toUri()).body(criada);
    }

    @Operation(summary = "Atualiza região (admin)")
    @PutMapping("/{id}")
    public EntityModel<RegiaoResponse> atualizar(@PathVariable Long id, @RequestBody @Valid RegiaoRequest dados) {
        return assembler.toModel(service.atualizar(id, dados));
    }

    @Operation(summary = "Remove região e seus pontos (admin); 409 se houver leituras ou alertas vinculados")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}