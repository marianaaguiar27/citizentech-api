package br.com.fiap.citizentech.dto;

import br.com.fiap.citizentech.model.StatusOcorrencia;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull StatusOcorrencia status) {
}