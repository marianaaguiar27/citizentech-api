package br.com.fiap.citizentech.controller;

import br.com.fiap.citizentech.dto.*;
import br.com.fiap.citizentech.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação", description = "Cadastro de moradores e login com JWT")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @Operation(summary = "Cadastra um morador (o papel é sempre morador)")
    @SecurityRequirements
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioResponse> registrar(@RequestBody @Valid RegistroRequest dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dados));
    }

    @Operation(summary = "Autentica e devolve o token JWT (validade de 1 hora)")
    @SecurityRequirements
    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest dados) {
        return service.login(dados);
    }
}