package br.com.fiap.citizentech.dto;

import br.com.fiap.citizentech.model.Alerta;
import br.com.fiap.citizentech.model.NivelRisco;
import br.com.fiap.citizentech.model.StatusAlerta;

import java.time.LocalDateTime;

public record AlertaResponse(Long id, NivelRisco nivel, String mensagem, StatusAlerta status,
                             LocalDateTime criadoEm, Long regiaoId, String regiaoNome) {

    public static AlertaResponse from(Alerta a) {
        return new AlertaResponse(a.getId(), a.getNivel(), a.getMensagem(), a.getStatus(),
                a.getCriadoEm(), a.getRegiao().getId(), a.getRegiao().getNome());
    }
}