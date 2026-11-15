package br.com.fiap.citizentech.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public record LeituraRequest(
        @NotNull Long pontoId,
        @NotNull @PositiveOrZero Double nivelAguaCm,
        @PositiveOrZero Double chuvaMm,
        @PastOrPresent LocalDateTime dataHora // opcional: se vazio, usa o horário atual
) {
}