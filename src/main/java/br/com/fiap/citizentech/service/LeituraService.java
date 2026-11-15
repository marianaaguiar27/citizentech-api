package br.com.fiap.citizentech.service;

import br.com.fiap.citizentech.dto.LeituraRegistradaResponse;
import br.com.fiap.citizentech.dto.LeituraRequest;
import br.com.fiap.citizentech.dto.LeituraResponse;
import br.com.fiap.citizentech.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.exception.RegraNegocioException;
import br.com.fiap.citizentech.model.*;
import br.com.fiap.citizentech.repository.AlertaRepository;
import br.com.fiap.citizentech.repository.LeituraRepository;
import br.com.fiap.citizentech.repository.PontoMonitoramentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LeituraService {

    // Janela usada para considerar o "pico recente" de água na região
    private static final int JANELA_HORAS = 6;

    private final LeituraRepository repository;
    private final PontoMonitoramentoRepository pontoRepository;
    private final AlertaRepository alertaRepository;

    @Transactional(readOnly = true)
    public Page<LeituraResponse> listar(Long pontoId, Pageable pageable) {
        Page<Leitura> pagina = (pontoId == null)
                ? repository.findAll(pageable)
                : repository.findByPontoId(pontoId, pageable);
        return pagina.map(LeituraResponse::from);
    }

    @Transactional(readOnly = true)
    public LeituraResponse buscar(Long id) {
        return LeituraResponse.from(repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Leitura", id)));
    }

    @Transactional(readOnly = true)
    public LeituraResponse buscarUltima(Long pontoId) {
        return repository.findFirstByPontoIdOrderByDataHoraDesc(pontoId)
                .map(LeituraResponse::from)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhuma leitura registrada para o ponto " + pontoId));
    }

    @Transactional
    public LeituraRegistradaResponse registrar(LeituraRequest dados) {
        PontoMonitoramento ponto = pontoRepository.findById(dados.pontoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ponto de monitoramento", dados.pontoId()));

        if (!ponto.getAtivo()) {
            throw new RegraNegocioException("O ponto '" + ponto.getNome() + "' está inativo e não aceita leituras");
        }

        Leitura leitura = new Leitura();
        leitura.setPonto(ponto);
        leitura.setNivelAguaCm(dados.nivelAguaCm());
        leitura.setChuvaMm(dados.chuvaMm());
        leitura.setDataHora(dados.dataHora()); // null -> @PrePersist usa agora
        repository.save(leitura);

        // Risco da região = pico de água nas últimas horas (inclui a leitura recém salva)
        Regiao regiao = ponto.getRegiao();
        double pico = repository
                .findMaiorNivelAgua(regiao.getId(), LocalDateTime.now().minusHours(JANELA_HORAS))
                .orElse(leitura.getNivelAguaCm());
        NivelRisco risco = NivelRisco.classificar(pico);
        regiao.setNivelRisco(risco);

        // Gera alerta só se o risco é alto e ainda não há um ativo no mesmo nível
        boolean alertaGerado = false;
        if (risco.exigeAlerta() && !alertaRepository
                .existsByRegiaoIdAndNivelAndStatus(regiao.getId(), risco, StatusAlerta.ATIVO)) {
            Alerta alerta = new Alerta();
            alerta.setNivel(risco);
            alerta.setMensagem(String.format("Risco %s em %s: nível da água em %.0f cm no ponto %s",
                    risco, regiao.getNome(), leitura.getNivelAguaCm(), ponto.getNome()));
            alerta.setRegiao(regiao);
            alerta.setLeitura(leitura);
            alertaRepository.save(alerta);
            alertaGerado = true;
        }

        return new LeituraRegistradaResponse(LeituraResponse.from(leitura), risco, alertaGerado);
    }
}