package br.com.fiap.citizentech.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.LocalDateTime;

public final class RespostaErroJson {

    private RespostaErroJson() {
    }

    public static void escrever(HttpServletRequest req, HttpServletResponse res,
                                HttpStatus status, String mensagem) throws IOException {
        res.setStatus(status.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\",\"campos\":[]}",
                LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                escapar(mensagem), escapar(req.getRequestURI())));
    }

    private static String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}