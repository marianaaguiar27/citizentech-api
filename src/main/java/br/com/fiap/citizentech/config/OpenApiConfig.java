package br.com.fiap.citizentech.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "AlertaBairro API", version = "1.0.0",
                description = "API de zeladoria colaborativa e alertas de risco para áreas vulneráveis"),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP,
        scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {

    // Documenta as respostas dos endpoints
    @Bean
    OpenApiCustomizer respostasPadrao() {
        return openApi -> openApi.getPaths().forEach((caminho, item) ->
                item.readOperations().forEach(op -> {
                    var respostas = op.getResponses();
                    respostas.putIfAbsent("400", new ApiResponse().description("Dados inválidos"));
                    respostas.putIfAbsent("500", new ApiResponse().description("Erro interno"));
                    if (!caminho.startsWith("/api/auth")) {
                        respostas.putIfAbsent("401", new ApiResponse().description("Token ausente, inválido ou expirado"));
                        respostas.putIfAbsent("403", new ApiResponse().description("Papel sem permissão"));
                    }
                }));
    }
}