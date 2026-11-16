package br.com.fiap.citizentech.dto;

import br.com.fiap.citizentech.model.TipoOcorrencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OcorrenciaRequest(
        @NotNull TipoOcorrencia tipo,
        @NotBlank @Size(min = 10, max = 500) String descricao,
        @NotNull Long regiaoId
) {
}