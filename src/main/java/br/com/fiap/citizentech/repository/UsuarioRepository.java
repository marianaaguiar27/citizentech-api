package br.com.fiap.citizentech.repository;

import br.com.fiap.citizentech.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Usado no login (JWT) e na validação de cadastro
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}