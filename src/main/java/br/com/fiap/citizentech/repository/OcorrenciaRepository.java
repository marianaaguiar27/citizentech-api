package br.com.fiap.citizentech.repository;

import br.com.fiap.citizentech.dto.ContagemPorTipo;
import br.com.fiap.citizentech.model.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// JpaSpecificationExecutor habilita findAll(Specification, Pageable)
public interface OcorrenciaRepository
        extends JpaRepository<Ocorrencia, Long>, JpaSpecificationExecutor<Ocorrencia> {

    // JPQL com agregação: quantas ocorrências de cada tipo existem na região
    @Query("""
            select new br.com.fiap.citizentech.dto.ContagemPorTipo(o.tipo, count(o))
            from Ocorrencia o
            where o.regiao.id = :regiaoId
            group by o.tipo
            """)
    List<ContagemPorTipo> contarPorTipo(@Param("regiaoId") Long regiaoId);
}