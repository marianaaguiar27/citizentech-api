package br.com.fiap.citizentech.service;

import br.com.fiap.citizentech.dto.RegiaoRequest;
import br.com.fiap.citizentech.dto.RegiaoResponse;
import br.com.fiap.citizentech.dto.RiscoRegiaoResponse;
import br.com.fiap.citizentech.exception.ConflitoException;
import br.com.fiap.citizentech.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.model.NivelRisco;
import br.com.fiap.citizentech.model.Regiao;
import br.com.fiap.citizentech.model.StatusAlerta;
import br.com.fiap.citizentech.repository.AlertaRepository;
import br.com.fiap.citizentech.repository.LeituraRepository;
import br.com.fiap.citizentech.repository.RegiaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegiaoService {

    private final RegiaoRepository repository;
    private final LeituraRepository leituraRepository;
    private final AlertaRepository alertaRepository;

    @Transactional(readOnly = true)
    public Page<RegiaoResponse> listar(NivelRisco nivelRisco, Pageable pageable) {
        Page<Regiao> pagina = (nivelRisco == null)
                ? repository.findAll(pageable)
                : repository.findByNivelRisco(nivelRisco, pageable);
        return pagina.map(RegiaoResponse::from);
    }

    @Cacheable(value = "regioes", key = "#id")
    @Transactional(readOnly = true)
    public RegiaoResponse buscar(Long id) {
        return RegiaoResponse.from(buscarEntidade(id));
    }

    // Consulta de risco: o pico de água da janela vem da JPQL da Etapa 3
    @Cacheable(value = "riscoRegiao", key = "#id")
    @Transactional(readOnly = true)
    public RiscoRegiaoResponse consultarRisco(Long id) {
        Regiao regiao = buscarEntidade(id);
        Double pico = leituraRepository
                .findMaiorNivelAgua(id, LocalDateTime.now().minusHours(LeituraService.JANELA_HORAS))
                .orElse(null);
        NivelRisco risco = (pico == null) ? NivelRisco.BAIXO : NivelRisco.classificar(pico);
        long ativos = alertaRepository.countByRegiaoIdAndStatus(id, StatusAlerta.ATIVO);
        return new RiscoRegiaoResponse(regiao.getId(), regiao.getNome(), risco, pico,
                LeituraService.JANELA_HORAS, ativos);
    }

    @Transactional
    public RegiaoResponse criar(RegiaoRequest dados) {
        if (repository.existsByNomeIgnoreCaseAndCidadeIgnoreCase(dados.nome(), dados.cidade())) {
            throw new ConflitoException("Já existe a região '" + dados.nome() + "' em " + dados.cidade());
        }
        Regiao regiao = new Regiao();
        preencher(regiao, dados);
        return RegiaoResponse.from(repository.save(regiao));
    }

    @Caching(evict = {
            @CacheEvict(value = "regioes", key = "#id"),
            @CacheEvict(value = "riscoRegiao", key = "#id")
    })
    @Transactional
    public RegiaoResponse atualizar(Long id, RegiaoRequest dados) {
        Regiao regiao = buscarEntidade(id);
        preencher(regiao, dados);
        return RegiaoResponse.from(regiao); // entidade gerenciada: o commit grava a alteração
    }

    @Caching(evict = {
            @CacheEvict(value = "regioes", key = "#id"),
            @CacheEvict(value = "riscoRegiao", key = "#id")
    })
    @Transactional
    public void deletar(Long id) {
        repository.delete(buscarEntidade(id));
        repository.flush();
    }

    private Regiao buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Região", id));
    }

    private void preencher(Regiao regiao, RegiaoRequest dados) {
        regiao.setNome(dados.nome());
        regiao.setCidade(dados.cidade());
        regiao.setUf(dados.uf().toUpperCase());
        if (dados.nivelRisco() != null) {
            regiao.setNivelRisco(dados.nivelRisco());
        }
    }
}