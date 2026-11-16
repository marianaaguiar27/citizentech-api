package br.com.fiap.citizentech.config;

import br.com.fiap.citizentech.security.JwtAuthFilter;
import br.com.fiap.citizentech.security.JwtService;
import br.com.fiap.citizentech.security.RespostaErroJson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // API stateless com token no cabeçalho: sem cookie, sem CSRF
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // públicos
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health/**", "/actuator/info").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                        // leitura: qualquer usuário autenticado
                        .requestMatchers(HttpMethod.GET, "/api/**").authenticated()

                        // moradores também reportam ocorrências
                        .requestMatchers(HttpMethod.POST, "/api/ocorrencias").authenticated()

                        // Defesa Civil e Admin operam o dia a dia
                        .requestMatchers(HttpMethod.PATCH, "/api/ocorrencias/*/status").hasAnyRole("DEFESA_CIVIL", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/ocorrencias/*").hasAnyRole("DEFESA_CIVIL", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/leituras").hasAnyRole("DEFESA_CIVIL", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/alertas").hasAnyRole("DEFESA_CIVIL", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/alertas/*/resolver").hasAnyRole("DEFESA_CIVIL", "ADMIN")

                        // o que sobrou em /api (criar/editar/apagar regiões e pontos, apagar alertas e ocorrências)
                        .requestMatchers("/api/**").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> RespostaErroJson.escrever(req, res,
                                HttpStatus.UNAUTHORIZED, "Autenticação necessária: envie um token JWT válido no cabeçalho Authorization"))
                        .accessDeniedHandler((req, res, ex) -> RespostaErroJson.escrever(req, res,
                                HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este recurso")))
                .addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origins}") List<String> origens) {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOriginPatterns(origens);
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        cors.setExposedHeaders(List.of("Location"));
        cors.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        fonte.registerCorsConfiguration("/**", cors);
        return fonte;
    }
}