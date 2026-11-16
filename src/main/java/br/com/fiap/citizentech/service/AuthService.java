package br.com.fiap.citizentech.service;

import br.com.fiap.citizentech.dto.*;
import br.com.fiap.citizentech.exception.ConflitoException;
import br.com.fiap.citizentech.exception.RecursoNaoEncontradoException;
import br.com.fiap.citizentech.model.Papel;
import br.com.fiap.citizentech.model.Usuario;
import br.com.fiap.citizentech.repository.RegiaoRepository;
import br.com.fiap.citizentech.repository.UsuarioRepository;
import br.com.fiap.citizentech.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RegiaoRepository regiaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public UsuarioResponse registrar(RegistroRequest dados) {
        String email = dados.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflitoException("Já existe um usuário cadastrado com o e-mail " + email);
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dados.nome());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(dados.senha())); // só o hash BCrypt é gravado
        usuario.setPapel(Papel.MORADOR); // cadastro público nunca escolhe o papel
        if (dados.regiaoId() != null) {
            usuario.setRegiao(regiaoRepository.findById(dados.regiaoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Região", dados.regiaoId())));
        }
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest dados) {
        String email = dados.email().trim().toLowerCase();

        // Lança BadCredentialsException se a senha ou o e-mail estiverem errados
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, dados.senha()));

        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        return new TokenResponse(jwtService.gerar(usuario), "Bearer", jwtService.getExpiracaoSegundos(),
                usuario.getId(), usuario.getNome(), usuario.getPapel());
    }
}