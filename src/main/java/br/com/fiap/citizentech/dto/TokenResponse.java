package br.com.fiap.citizentech.dto;

import br.com.fiap.citizentech.model.Papel;

public record TokenResponse(String token, String tipo, long expiraEmSegundos,
                            Long usuarioId, String nome, Papel papel) {
}