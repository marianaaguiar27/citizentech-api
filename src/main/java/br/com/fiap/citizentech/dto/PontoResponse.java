package br.com.fiap.citizentech.dto;

import br.com.fiap.citizentech.model.PontoMonitoramento;

public record PontoResponse(Long id, String nome, Double latitude, Double longitude,
                            Boolean ativo, Long regiaoId) {

    public static PontoResponse from(PontoMonitoramento p) {
        // getRegiao().getId() não dispara consulta: o id já está no proxy LAZY
        return new PontoResponse(p.getId(), p.getNome(), p.getLatitude(), p.getLongitude(),
                p.getAtivo(), p.getRegiao().getId());
    }
}