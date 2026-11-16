package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.assembler.RegiaoModelAssembler;
import br.com.fiap.citizentech.dto.RegiaoRequest;
import br.com.fiap.citizentech.dto.RegiaoResponse;
import br.com.fiap.citizentech.dto.RiscoRegiaoResponse;
import br.com.fiap.citizentech.model.NivelRisco;
import br.com.fiap.citizentech.service.RegiaoService;
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

@RestController
@RequestMapping("/api/regioes")
@RequiredArgsConstructor
public class RegiaoController {

    private final RegiaoService service;
    private final RegiaoModelAssembler assembler;
    private final PagedResourcesAssembler<RegiaoResponse> pagedAssembler;

    @GetMapping
    public PagedModel<EntityModel<RegiaoResponse>> listar(
            @RequestParam(required = false) NivelRisco nivelRisco,
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return pagedAssembler.toModel(service.listar(nivelRisco, pageable), assembler);
    }

    @GetMapping("/{id}")
    public EntityModel<RegiaoResponse> buscar(@PathVariable Long id) {
        return assembler.toModel(service.buscar(id));
    }

    @GetMapping("/{id}/risco")
    public RiscoRegiaoResponse risco(@PathVariable Long id) {
        return service.consultarRisco(id);
    }

    @PostMapping
    public ResponseEntity<EntityModel<RegiaoResponse>> criar(@RequestBody @Valid RegiaoRequest dados) {
        EntityModel<RegiaoResponse> criada = assembler.toModel(service.criar(dados));
        return ResponseEntity.created(criada.getRequiredLink("self").toUri()).body(criada);
    }

    @PutMapping("/{id}")
    public EntityModel<RegiaoResponse> atualizar(@PathVariable Long id, @RequestBody @Valid RegiaoRequest dados) {
        return assembler.toModel(service.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}