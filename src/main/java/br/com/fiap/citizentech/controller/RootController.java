package br.com.fiap.citizentech.controller;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api")
public class RootController {

    @GetMapping
    public RepresentationModel<?> raiz() {
        RepresentationModel<?> raiz = new RepresentationModel<>();
        raiz.add(linkTo(RootController.class).withSelfRel(),
                linkTo(RegiaoController.class).withRel("regioes"),
                linkTo(PontoMonitoramentoController.class).withRel("pontos"),
                linkTo(LeituraController.class).withRel("leituras"),
                linkTo(AlertaController.class).slash("ativos").withRel("alertas-ativos"),
                linkTo(OcorrenciaController.class).withRel("ocorrencias"));
        return raiz;
    }
}