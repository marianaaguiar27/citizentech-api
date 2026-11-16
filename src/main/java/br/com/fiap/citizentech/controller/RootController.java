package br.com.fiap.citizentech.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Tag(name = "Raiz", description = "Ponto de entrada Hateoas da API")
@RestController
@RequestMapping("/api")
public class RootController {

    @Operation(summary = "Links para as coleções da API")
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