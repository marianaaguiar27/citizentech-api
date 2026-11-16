package br.com.fiap.citizentech.dto;

import br.com.fiap.citizentech.model.NivelRisco;

public record RiscoRegiaoResponse(Long regiaoId, String regiaoNome, NivelRisco nivelRisco,
                                  Double picoNivelAguaCm, int janelaHoras, long alertasAtivos) {
}