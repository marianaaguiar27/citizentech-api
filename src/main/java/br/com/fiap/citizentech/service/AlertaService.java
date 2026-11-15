package br.com.fiap.citizentech.service;

import br.com.fiap.citizentech.dto.AlertaRequest;
import br.com.fiap.citizentech.dto.AlertaResponse;
import br.com.fiap.citizentech.exception.ConflitoException;
import br.com.fiap.citizentech.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.model.Alerta;
import br.com.fiap.citizentech.model.Regiao;
import br.com.fiap.citizentech.model.StatusAlerta;
import br.com.fiap.citizentech.repository.AlertaRepository;
import br.com.fiap.citizentech.repository.RegiaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository repository;
    private final RegiaoRepository regiaoRepository;

    @Transactional(readOnly = true)
    public Page<AlertaResponse> listarPorRegiao(Long regiaoId, StatusAlerta status, Pageable pageable) {
        if (!regiaoRepository.existsById(regiaoId)) {
            throw new RecursoNaoEncontradoException("Região", regiaoId);
        }
        return repository.findByRegiaoIdAndStatus(regiaoId, status, pageable).map(AlertaResponse::from);
    }

    @Transactional(readOnly = true)
    public List<AlertaResponse> listarAtivos() {
        return repository.findAtivosComRegiao().stream().map(AlertaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public AlertaResponse buscar(Long id) {
        return AlertaResponse.from(buscarEntidade(id));
    }

    // Alerta manual, emitido pela Defesa Civil
    @Transactional
    public AlertaResponse criar(AlertaRequest dados) {
        Regiao regiao = regiaoRepository.findById(dados.regiaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Região", dados.regiaoId()));
        Alerta alerta = new Alerta();
        alerta.setNivel(dados.nivel());
        alerta.setMensagem(dados.mensagem());
        alerta.setRegiao(regiao);
        return AlertaResponse.from(repository.save(alerta));
    }

    @Transactional
    public AlertaResponse resolver(Long id) {
        Alerta alerta = buscarEntidade(id);
        if (alerta.getStatus() == StatusAlerta.RESOLVIDO) {
            throw new ConflitoException("O alerta " + id + " já está resolvido");
        }
        alerta.setStatus(StatusAlerta.RESOLVIDO);
        return AlertaResponse.from(alerta);
    }

    @Transactional
    public void deletar(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Alerta buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta", id));
    }
}