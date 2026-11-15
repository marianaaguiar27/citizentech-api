package br.com.fiap.citizentech.dto;

import br.com.fiap.citizentech.model.NivelRisco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegiaoRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Size(max = 80) String cidade,
        @NotBlank @Size(min = 2, max = 2) String uf,
        NivelRisco nivelRisco
) {
}