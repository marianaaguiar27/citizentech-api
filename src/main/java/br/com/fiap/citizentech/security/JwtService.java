package br.com.fiap.citizentech.security;

import br.com.fiap.citizentech.model.Papel;
import br.com.fiap.citizentech.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracaoMs;

    public JwtService(@Value("${app.jwt.secret}") String segredo,
                      @Value("${app.jwt.expiration-ms}") long expiracaoMs) {
        byte[] bytes = segredo.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret precisa ter no mínimo 32 caracteres");
        }
        this.chave = Keys.hmacShaKeyFor(bytes);
        this.expiracaoMs = expiracaoMs;
    }

    public String gerar(Usuario usuario) {
        Date agora = new Date();
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("uid", usuario.getId())
                .claim("papel", usuario.getPapel().name())
                .issuedAt(agora)
                .expiration(new Date(agora.getTime() + expiracaoMs))
                .signWith(chave)
                .compact();
    }

    // Token inválido, adulterado ou expirado: devolve vazio (a requisição segue sem autenticação)
    public Optional<UsuarioLogado> validar(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(chave).build()
                    .parseSignedClaims(token).getPayload();
            Long id = ((Number) claims.get("uid")).longValue();
            Papel papel = Papel.valueOf(claims.get("papel", String.class));
            return Optional.of(new UsuarioLogado(id, claims.getSubject(), papel));
        } catch (JwtException | IllegalArgumentException | ClassCastException | NullPointerException e) {
            return Optional.empty();
        }
    }

    public long getExpiracaoSegundos() {
        return expiracaoMs / 1000;
    }
}