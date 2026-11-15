package br.com.fiap.citizentech.repository;

import br.com.fiap.citizentech.model.Leitura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LeituraRepository extends JpaRepository<Leitura, Long> {

    // Query method derivado: histórico paginado de um ponto
    Page<Leitura> findByPontoId(Long pontoId, Pageable pageable);

    // Query method derivado: última leitura de um ponto
    Optional<Leitura> findFirstByPontoIdOrderByDataHoraDesc(Long pontoId);

    // JPQL: leituras recentes de uma região (navega ponto -> região)
    @Query("""
            select l from Leitura l
            where l.ponto.regiao.id = :regiaoId
              and l.dataHora >= :desde
            order by l.dataHora desc
            """)
    List<Leitura> findRecentesPorRegiao(@Param("regiaoId") Long regiaoId,
                                        @Param("desde") LocalDateTime desde);

    // JPQL: maior nível de água da região no período (base da classificação de risco)
    @Query("""
            select max(l.nivelAguaCm) from Leitura l
            where l.ponto.regiao.id = :regiaoId
              and l.dataHora >= :desde
            """)
    Optional<Double> findMaiorNivelAgua(@Param("regiaoId") Long regiaoId,
                                        @Param("desde") LocalDateTime desde);
}