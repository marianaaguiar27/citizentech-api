package br.com.fiap.citizentech.dto;

import br.com.fiap.citizentech.model.Papel;
import br.com.fiap.citizentech.model.Usuario;

public record UsuarioResponse(Long id, String nome, String email, Papel papel) {

    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPapel());
    }
}