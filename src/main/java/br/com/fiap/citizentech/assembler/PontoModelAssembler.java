package br.com.fiap.citizentech.assembler;

import br.com.fiap.citizentech.controller.*;
import br.com.fiap.citizentech.dto.PontoResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static br.com.fiap.citizentech.assembler.LinkUtil.comFiltro;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class PontoModelAssembler
        implements RepresentationModelAssembler<PontoResponse, EntityModel<PontoResponse>> {

    @Override
    public EntityModel<PontoResponse> toModel(PontoResponse p) {
        return EntityModel.of(p,
                linkTo(PontoMonitoramentoController.class).slash(p.id()).withSelfRel(),
                linkTo(RegiaoController.class).slash(p.regiaoId()).withRel("regiao"),
                comFiltro(LeituraController.class, "pontoId", p.id(), "leituras"),
                linkTo(LeituraController.class).slash("ponto").slash(p.id()).slash("ultima")
                        .withRel("ultima-leitura"),
                linkTo(PontoMonitoramentoController.class).withRel("pontos"));
    }
}