package br.com.fiap.citizentech.service;

import br.com.fiap.citizentech.dto.RegiaoRequest;
import br.com.fiap.citizentech.dto.RegiaoResponse;
import br.com.fiap.citizentech.exception.ConflitoException;
import br.com.fiap.citizentech.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.model.NivelRisco;
import br.com.fiap.citizentech.model.Regiao;
import br.com.fiap.citizentech.repository.RegiaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegiaoService {

    private final RegiaoRepository repository;

    @Transactional(readOnly = true)
    public Page<RegiaoResponse> listar(NivelRisco nivelRisco, Pageable pageable) {
        Page<Regiao> pagina = (nivelRisco == null)
                ? repository.findAll(pageable)
                : repository.findByNivelRisco(nivelRisco, pageable);
        return pagina.map(RegiaoResponse::from);
    }

    @Transactional(readOnly = true)
    public RegiaoResponse buscar(Long id) {
        return RegiaoResponse.from(buscarEntidade(id));
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

    @Transactional
    public RegiaoResponse atualizar(Long id, RegiaoRequest dados) {
        Regiao regiao = buscarEntidade(id);
        preencher(regiao, dados);
        return RegiaoResponse.from(regiao); // entidade gerenciada: o commit grava a alteração
    }

    @Transactional
    public void deletar(Long id) {
        repository.delete(buscarEntidade(id));
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