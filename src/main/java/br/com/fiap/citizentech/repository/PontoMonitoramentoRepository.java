package br.com.fiap.citizentech.repository;

import br.com.fiap.citizentech.model.PontoMonitoramento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PontoMonitoramentoRepository extends JpaRepository<PontoMonitoramento, Long> {

    // Query method derivado: pontos de uma região
    Page<PontoMonitoramento> findByRegiaoId(Long regiaoId, Pageable pageable);
}