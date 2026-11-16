package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.dto.*;
import br.com.fiap.citizentech.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioResponse> registrar(@RequestBody @Valid RegistroRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dados));
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest dados) {
        return service.login(dados);
    }
}