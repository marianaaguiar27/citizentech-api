package br.com.fiap.citizentech.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<CampoErro> campos
) {
    public record CampoErro(String campo, String mensagem) {
    }
}