package br.com.fiap.citizentech.dto;

import br.com.fiap.citizentech.model.TipoOcorrencia;

public record ContagemPorTipo(TipoOcorrencia tipo, Long total) {
}