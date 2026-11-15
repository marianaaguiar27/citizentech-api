package br.com.fiap.citizentech.repository;

import br.com.fiap.citizentech.model.NivelRisco;
import br.com.fiap.citizentech.model.Regiao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegiaoRepository extends JpaRepository<Regiao, Long> {

    // Query method derivado: lista as regiões por nível de risco (paginado)
    Page<Regiao> findByNivelRisco(NivelRisco nivelRisco, Pageable pageable);

    // Regra de negócio: não permitir região duplicada na mesma cidade
    boolean existsByNomeIgnoreCaseAndCidadeIgnoreCase(String nome, String cidade);
}