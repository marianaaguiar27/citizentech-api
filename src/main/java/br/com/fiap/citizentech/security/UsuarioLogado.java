package br.com.fiap.citizentech.security;

import br.com.fiap.citizentech.model.Papel;

// Usuário autenticado, montado a partir do token em cada requisição
public record UsuarioLogado(Long id, String email, Papel papel) {
}