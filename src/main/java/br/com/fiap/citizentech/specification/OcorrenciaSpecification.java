package br.com.fiap.citizentech.specification;

import br.com.fiap.citizentech.model.Ocorrencia;
import br.com.fiap.citizentech.model.StatusOcorrencia;
import br.com.fiap.citizentech.model.TipoOcorrencia;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OcorrenciaSpecification {

    private OcorrenciaSpecification() {
    }

    // Cada filtro só entra na consulta se vier preenchido
    public static Specification<Ocorrencia> filtrar(TipoOcorrencia tipo,
                                                    StatusOcorrencia status,
                                                    Long regiaoId,
                                                    LocalDateTime desde) {
        return (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();

            if (tipo != null) {
                filtros.add(cb.equal(root.get("tipo"), tipo));
            }
            if (status != null) {
                filtros.add(cb.equal(root.get("status"), status));
            }
            if (regiaoId != null) {
                filtros.add(cb.equal(root.get("regiao").get("id"), regiaoId));
            }
            if (desde != null) {
                filtros.add(cb.greaterThanOrEqualTo(root.get("criadoEm"), desde));
            }

            return cb.and(filtros.toArray(new Predicate[0]));
        };
    }
}