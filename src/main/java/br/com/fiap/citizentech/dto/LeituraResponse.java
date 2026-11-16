package br.com.fiap.citizentech.dto;

import br.com.fiap.citizentech.model.Leitura;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Relation(collectionRelation = "leituras", itemRelation = "leitura")
public record LeituraResponse(Long id, Long pontoId, Double nivelAguaCm,
                              Double chuvaMm, LocalDateTime dataHora) {

    public static LeituraResponse from(Leitura l) {
        return new LeituraResponse(l.getId(), l.getPonto().getId(),
                l.getNivelAguaCm(), l.getChuvaMm(), l.getDataHora());
    }
}