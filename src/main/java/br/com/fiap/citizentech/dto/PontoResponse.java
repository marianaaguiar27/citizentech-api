package br.com.fiap.citizentech.dto;

import br.com.fiap.citizentech.model.PontoMonitoramento;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "pontos", itemRelation = "ponto")
public record PontoResponse(Long id, String nome, Double latitude, Double longitude,
                            Boolean ativo, Long regiaoId) {

    public static PontoResponse from(PontoMonitoramento p) {
        return new PontoResponse(p.getId(), p.getNome(), p.getLatitude(), p.getLongitude(),
                p.getAtivo(), p.getRegiao().getId());
    }
}