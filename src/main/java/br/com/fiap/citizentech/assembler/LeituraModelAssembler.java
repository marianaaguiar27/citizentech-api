package br.com.fiap.citizentech.assembler;

import br.com.fiap.citizentech.controller.*;
import br.com.fiap.citizentech.dto.LeituraResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class LeituraModelAssembler
        implements RepresentationModelAssembler<LeituraResponse, EntityModel<LeituraResponse>> {

    @Override
    public EntityModel<LeituraResponse> toModel(LeituraResponse l) {
        return EntityModel.of(l,
                linkTo(LeituraController.class).slash(l.id()).withSelfRel(),
                linkTo(PontoMonitoramentoController.class).slash(l.pontoId()).withRel("ponto"),
                linkTo(LeituraController.class).withRel("leituras"));
    }
}