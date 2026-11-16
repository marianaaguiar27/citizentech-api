package br.com.fiap.citizentech.dto;

import br.com.fiap.citizentech.model.Ocorrencia;
import br.com.fiap.citizentech.model.StatusOcorrencia;
import br.com.fiap.citizentech.model.TipoOcorrencia;

import java.time.LocalDateTime;

public record OcorrenciaResponse(Long id, TipoOcorrencia tipo, String descricao,
                                 StatusOcorrencia status, LocalDateTime criadoEm,
                                 Long regiaoId, String regiaoNome,
                                 Long autorId, String autorNome) {

    public static OcorrenciaResponse from(Ocorrencia o) {
        return new OcorrenciaResponse(o.getId(), o.getTipo(), o.getDescricao(), o.getStatus(),
                o.getCriadoEm(), o.getRegiao().getId(), o.getRegiao().getNome(),
                o.getAutor().getId(), o.getAutor().getNome());
    }
}