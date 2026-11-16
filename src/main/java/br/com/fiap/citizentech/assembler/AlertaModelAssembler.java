package br.com.fiap.citizentech.assembler;

import br.com.fiap.citizentech.controller.*;
import br.com.fiap.citizentech.dto.AlertaResponse;
import br.com.fiap.citizentech.model.StatusAlerta;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class AlertaModelAssembler
        implements RepresentationModelAssembler<AlertaResponse, EntityModel<AlertaResponse>> {

    @Override
    public EntityModel<AlertaResponse> toModel(AlertaResponse a) {
        EntityModel<AlertaResponse> modelo = EntityModel.of(a,
                linkTo(AlertaController.class).slash(a.id()).withSelfRel(),
                linkTo(RegiaoController.class).slash(a.regiaoId()).withRel("regiao"),
                linkTo(AlertaController.class).slash("ativos").withRel("alertas-ativos"));

        // Ação possível apenas enquanto o alerta está ativo
        if (a.status() == StatusAlerta.ATIVO) {
            modelo.add(linkTo(AlertaController.class).slash(a.id()).slash("resolver")
                    .withRel("resolver").withTitle("PATCH"));
        }
        return modelo;
    }
}