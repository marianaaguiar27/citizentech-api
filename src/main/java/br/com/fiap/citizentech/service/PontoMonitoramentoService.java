package br.com.fiap.citizentech.service;

import br.com.fiap.citizentech.dto.PontoRequest;
import br.com.fiap.citizentech.dto.PontoResponse;
import br.com.fiap.citizentech.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.model.PontoMonitoramento;
import br.com.fiap.citizentech.model.Regiao;
import br.com.fiap.citizentech.repository.PontoMonitoramentoRepository;
import br.com.fiap.citizentech.repository.RegiaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PontoMonitoramentoService {

    private final PontoMonitoramentoRepository repository;
    private final RegiaoRepository regiaoRepository;

    @Transactional(readOnly = true)
    public Page<PontoResponse> listar(Long regiaoId, Pageable pageable) {
        Page<PontoMonitoramento> pagina = (regiaoId == null)
                ? repository.findAll(pageable)
                : repository.findByRegiaoId(regiaoId, pageable);
        return pagina.map(PontoResponse::from);
    }

    @Transactional(readOnly = true)
    public PontoResponse buscar(Long id) {
        return PontoResponse.from(buscarEntidade(id));
    }

    @Transactional
    public PontoResponse criar(PontoRequest dados) {
        PontoMonitoramento ponto = new PontoMonitoramento();
        preencher(ponto, dados);
        return PontoResponse.from(repository.save(ponto));
    }

    @Transactional
    public PontoResponse atualizar(Long id, PontoRequest dados) {
        PontoMonitoramento ponto = buscarEntidade(id);
        preencher(ponto, dados);
        return PontoResponse.from(ponto);
    }

    @Transactional
    public void deletar(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private PontoMonitoramento buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ponto de monitoramento", id));
    }

    private void preencher(PontoMonitoramento ponto, PontoRequest dados) {
        Regiao regiao = regiaoRepository.findById(dados.regiaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Região", dados.regiaoId()));
        ponto.setNome(dados.nome());
        ponto.setLatitude(dados.latitude());
        ponto.setLongitude(dados.longitude());
        ponto.setAtivo(dados.ativo() != null ? dados.ativo() : true);
        ponto.setRegiao(regiao);
    }
}