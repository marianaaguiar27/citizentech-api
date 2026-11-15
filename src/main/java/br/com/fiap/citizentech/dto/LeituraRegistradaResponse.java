package br.com.fiap.citizentech.dto;

import br.com.fiap.citizentech.model.NivelRisco;

public record LeituraRegistradaResponse(LeituraResponse leitura,
                                        NivelRisco riscoRegiao,
                                        boolean alertaGerado) {
}