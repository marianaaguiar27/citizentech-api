package br.com.fiap.citizentech.assembler;

import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

final class LinkUtil {

    private LinkUtil() {
    }

    static Link comFiltro(Class<?> controller, String parametro, Object valor, String rel) {
        String uri = linkTo(controller).toUriComponentsBuilder()
                .queryParam(parametro, valor)
                .build().toUriString();
        return Link.of(uri, rel);
    }
}