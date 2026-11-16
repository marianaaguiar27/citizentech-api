package br.com.fiap.citizentech.assembler;

import br.com.fiap.citizentech.controller.*;
import br.com.fiap.citizentech.dto.RegiaoResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static br.com.fiap.citizentech.assembler.LinkUtil.comFiltro;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class RegiaoModelAssembler
        implements RepresentationModelAssembler<RegiaoResponse, EntityModel<RegiaoResponse>> {

    @Override
    public EntityModel<RegiaoResponse> toModel(RegiaoResponse r) {
        return EntityModel.of(r,
                linkTo(RegiaoController.class).slash(r.id()).withSelfRel(),
                linkTo(RegiaoController.class).slash(r.id()).slash("risco").withRel("risco"),
                comFiltro(PontoMonitoramentoController.class, "regiaoId", r.id(), "pontos"),
                comFiltro(AlertaController.class, "regiaoId", r.id(), "alertas"),
                comFiltro(OcorrenciaController.class, "regiaoId", r.id(), "ocorrencias"),
                comFiltro(OcorrenciaController.class, "regiaoId", r.id(), "estatisticas")
                        .withHref(linkTo(OcorrenciaController.class).slash("estatisticas").toUriComponentsBuilder()
                                .queryParam("regiaoId", r.id()).build().toUriString()),
                linkTo(RegiaoController.class).withRel("regioes"));
    }
}