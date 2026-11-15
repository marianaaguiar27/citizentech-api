package br.com.fiap.citizentech.dto;

import br.com.fiap.citizentech.model.NivelRisco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlertaRequest(
        @NotNull Long regiaoId,
        @NotNull NivelRisco nivel,
        @NotBlank @Size(max = 255) String mensagem
) {
}