package br.com.fiap.citizentech.repository;

import br.com.fiap.citizentech.model.Alerta;
import br.com.fiap.citizentech.model.NivelRisco;
import br.com.fiap.citizentech.model.StatusAlerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    // Query method derivado: alertas de uma região por status
    Page<Alerta> findByRegiaoIdAndStatus(Long regiaoId, StatusAlerta status, Pageable pageable);

    // Evita criar alerta duplicado enquanto já existe um ativo no mesmo nível
    boolean existsByRegiaoIdAndNivelAndStatus(Long regiaoId, NivelRisco nivel, StatusAlerta status);

    long countByRegiaoIdAndStatus(Long regiaoId, StatusAlerta status);

    // JPQL com join fetch: traz a região junto e evita N+1 ao listar os alertas ativos
    @Query("""
            select a from Alerta a
            join fetch a.regiao
            where a.status = br.com.fiap.citizentech.model.StatusAlerta.ATIVO
            order by a.criadoEm desc
            """)
    List<Alerta> findAtivosComRegiao();

}