package br.com.fiap.citizentech.config;

import br.com.fiap.citizentech.model.*;
import br.com.fiap.citizentech.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// Dados de demonstração. Os usuários e senhas abaixo são APENAS de teste (constam no README).
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final RegiaoRepository regiaoRepository;
    private final LeituraRepository leituraRepository;
    private final AlertaRepository alertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final OcorrenciaRepository ocorrenciaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (regiaoRepository.count() > 0) {
            return;
        }

        // Regiões e pontos (cascade grava os pontos junto com a região)
        Regiao primavera = regiao("Jardim Primavera", "São Paulo", "SP", NivelRisco.ALTO);
        PontoMonitoramento corrego = ponto("Córrego Norte", -23.5505, -46.6333);
        primavera.adicionarPonto(corrego);
        primavera.adicionarPonto(ponto("Ponte da Av. Central", -23.5520, -46.6350));

        Regiao esperanca = regiao("Vila Esperança", "São Paulo", "SP", NivelRisco.BAIXO);
        PontoMonitoramento praca = ponto("Praça Baixa", -23.5600, -46.6400);
        esperanca.adicionarPonto(praca);

        Regiao morro = regiao("Morro Alto", "Santos", "SP", NivelRisco.MEDIO);
        PontoMonitoramento encosta = ponto("Encosta Leste", -23.9600, -46.3300);
        morro.adicionarPonto(encosta);

        regiaoRepository.save(primavera);
        regiaoRepository.save(esperanca);
        regiaoRepository.save(morro);

        // Leituras recentes (dentro da janela de 6h da regra de risco)
        double[] subindo = {20, 60, 90, 120, 135};
        Leitura ultimaPrimavera = null;
        for (int i = 0; i < subindo.length; i++) {
            ultimaPrimavera = leitura(corrego, subindo[i], 5 - i);
        }
        leitura(praca, 20, 3);
        leitura(praca, 30, 1);
        leitura(encosta, 55, 2);
        leitura(encosta, 70, 1);

        // Alerta correspondente ao risco ALTO de Jardim Primavera
        Alerta alerta = new Alerta();
        alerta.setNivel(NivelRisco.ALTO);
        alerta.setMensagem("Risco ALTO em Jardim Primavera: nível da água em 135 cm no ponto Córrego Norte");
        alerta.setRegiao(primavera);
        alerta.setLeitura(ultimaPrimavera);
        alertaRepository.save(alerta);

        // Usuários de teste, um por papel
        Usuario morador = usuario("Maria Moradora", "morador@alertabairro.com", "Morador@123", Papel.MORADOR, primavera);
        usuario("Carlos Defesa Civil", "defesa@alertabairro.com", "Defesa@123", Papel.DEFESA_CIVIL, null);
        usuario("Ana Admin", "admin@alertabairro.com", "Admin@123", Papel.ADMIN, null);

        // Ocorrências de exemplo
        ocorrencia(TipoOcorrencia.ALAGAMENTO, "Rua Principal alagada em frente ao número 120, água na altura do joelho",
                primavera, morador, StatusOcorrencia.EM_ANALISE);
        ocorrencia(TipoOcorrencia.BURACO, "Buraco grande na Rua das Flores, já causou dois acidentes",
                primavera, morador, StatusOcorrencia.ABERTA);
        ocorrencia(TipoOcorrencia.ILUMINACAO, "Três postes apagados na Travessa Esperança há uma semana",
                esperanca, morador, StatusOcorrencia.RESOLVIDA);

        log.info("Dados de exemplo carregados (3 regiões, 4 pontos, 9 leituras, 3 usuários, 3 ocorrências)");
    }

    private Regiao regiao(String nome, String cidade, String uf, NivelRisco risco) {
        Regiao r = new Regiao();
        r.setNome(nome);
        r.setCidade(cidade);
        r.setUf(uf);
        r.setNivelRisco(risco);
        return r;
    }

    private PontoMonitoramento ponto(String nome, double lat, double lon) {
        PontoMonitoramento p = new PontoMonitoramento();
        p.setNome(nome);
        p.setLatitude(lat);
        p.setLongitude(lon);
        return p;
    }

    private Leitura leitura(PontoMonitoramento ponto, double nivelCm, int horasAtras) {
        Leitura l = new Leitura();
        l.setPonto(ponto);
        l.setNivelAguaCm(nivelCm);
        l.setDataHora(LocalDateTime.now().minusHours(horasAtras));
        return leituraRepository.save(l);
    }

    private Usuario usuario(String nome, String email, String senha, Papel papel, Regiao regiao) {
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setSenha(passwordEncoder.encode(senha));
        u.setPapel(papel);
        u.setRegiao(regiao);
        return usuarioRepository.save(u);
    }

    private void ocorrencia(TipoOcorrencia tipo, String descricao, Regiao regiao,
                            Usuario autor, StatusOcorrencia status) {
        Ocorrencia o = new Ocorrencia();
        o.setTipo(tipo);
        o.setDescricao(descricao);
        o.setRegiao(regiao);
        o.setAutor(autor);
        o.setStatus(status);
        ocorrenciaRepository.save(o);
    }
}