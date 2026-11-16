package br.com.fiap.citizentech.service;

import br.com.fiap.citizentech.dto.ContagemPorTipo;
import br.com.fiap.citizentech.dto.OcorrenciaRequest;
import br.com.fiap.citizentech.dto.OcorrenciaResponse;
import br.com.fiap.citizentech.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.exception.RegraNegocioException;
import br.com.fiap.citizentech.model.*;
import br.com.fiap.citizentech.repository.OcorrenciaRepository;
import br.com.fiap.citizentech.repository.RegiaoRepository;
import br.com.fiap.citizentech.repository.UsuarioRepository;
import br.com.fiap.citizentech.specification.OcorrenciaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcorrenciaService {

    private final OcorrenciaRepository repository;
    private final RegiaoRepository regiaoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Page<OcorrenciaResponse> filtrar(TipoOcorrencia tipo, StatusOcorrencia status,
                                            Long regiaoId, LocalDateTime desde, Pageable pageable) {
        return repository.findAll(OcorrenciaSpecification.filtrar(tipo, status, regiaoId, desde), pageable)
                .map(OcorrenciaResponse::from);
    }

    @Transactional(readOnly = true)
    public OcorrenciaResponse buscar(Long id) {
        return OcorrenciaResponse.from(buscarEntidade(id));
    }

    @Cacheable(value = "estatisticasOcorrencias", key = "#regiaoId")
    @Transactional(readOnly = true)
    public List<ContagemPorTipo> estatisticas(Long regiaoId) {
        if (!regiaoRepository.existsById(regiaoId)) {
            throw new RecursoNaoEncontradoException("Região", regiaoId);
        }
        return repository.contarPorTipo(regiaoId);
    }

    @CacheEvict(value = "estatisticasOcorrencias", allEntries = true)
    @Transactional
    public OcorrenciaResponse criar(OcorrenciaRequest dados) {
        Regiao regiao = regiaoRepository.findById(dados.regiaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Região", dados.regiaoId()));
        Usuario autor = usuarioRepository.findById(dados.autorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", dados.autorId()));

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setTipo(dados.tipo());
        ocorrencia.setDescricao(dados.descricao());
        ocorrencia.setRegiao(regiao);
        ocorrencia.setAutor(autor);
        return OcorrenciaResponse.from(repository.save(ocorrencia));
    }

    // Edita só o conteúdo. O autor nunca muda e o status tem endpoint próprio.
    @CacheEvict(value = "estatisticasOcorrencias", allEntries = true)
    @Transactional
    public OcorrenciaResponse atualizar(Long id, OcorrenciaRequest dados) {
        Ocorrencia ocorrencia = buscarEntidade(id);
        if (ocorrencia.getStatus() == StatusOcorrencia.RESOLVIDA) {
            throw new RegraNegocioException("Ocorrência já resolvida não pode ser editada");
        }
        Regiao regiao = regiaoRepository.findById(dados.regiaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Região", dados.regiaoId()));
        ocorrencia.setTipo(dados.tipo());
        ocorrencia.setDescricao(dados.descricao());
        ocorrencia.setRegiao(regiao);
        return OcorrenciaResponse.from(ocorrencia);
    }

    // Regra: o fluxo só avança (ABERTA -> EM_ANALISE -> RESOLVIDA) e RESOLVIDA é final
    @Transactional
    public OcorrenciaResponse atualizarStatus(Long id, StatusOcorrencia novo) {
        Ocorrencia ocorrencia = buscarEntidade(id);
        StatusOcorrencia atual = ocorrencia.getStatus();

        if (atual == StatusOcorrencia.RESOLVIDA) {
            throw new RegraNegocioException("Ocorrência resolvida não pode ser reaberta");
        }
        if (novo.ordinal() <= atual.ordinal()) {
            throw new RegraNegocioException("Transição inválida: " + atual + " -> " + novo);
        }
        ocorrencia.setStatus(novo);
        return OcorrenciaResponse.from(ocorrencia);
    }

    @CacheEvict(value = "estatisticasOcorrencias", allEntries = true)
    @Transactional
    public void deletar(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Ocorrencia buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ocorrência", id));
    }
}