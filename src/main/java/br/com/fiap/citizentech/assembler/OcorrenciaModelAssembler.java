package br.com.fiap.citizentech.assembler;

import br.com.fiap.citizentech.controller.*;
import br.com.fiap.citizentech.dto.OcorrenciaResponse;
import br.com.fiap.citizentech.model.StatusOcorrencia;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class OcorrenciaModelAssembler
        implements RepresentationModelAssembler<OcorrenciaResponse, EntityModel<OcorrenciaResponse>> {

    @Override
    public EntityModel<OcorrenciaResponse> toModel(OcorrenciaResponse o) {
        EntityModel<OcorrenciaResponse> modelo = EntityModel.of(o,
                linkTo(OcorrenciaController.class).slash(o.id()).withSelfRel(),
                linkTo(RegiaoController.class).slash(o.regiaoId()).withRel("regiao"),
                linkTo(OcorrenciaController.class).withRel("ocorrencias"));

        // O fluxo só avança, então só oferecemos as ações enquanto não está RESOLVIDA
        if (o.status() != StatusOcorrencia.RESOLVIDA) {
            StatusOcorrencia proximo = (o.status() == StatusOcorrencia.ABERTA)
                    ? StatusOcorrencia.EM_ANALISE : StatusOcorrencia.RESOLVIDA;
            modelo.add(linkTo(OcorrenciaController.class).slash(o.id()).slash("status")
                    .withRel("avancar-status").withTitle("PATCH -> " + proximo));
            modelo.add(linkTo(OcorrenciaController.class).slash(o.id())
                    .withRel("editar").withTitle("PUT"));
        }
        return modelo;
    }
}