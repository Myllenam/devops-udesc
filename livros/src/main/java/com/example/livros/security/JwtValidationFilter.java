package com.example.livros.security;

import com.example.livros.client.AuthServiceClient;
import com.example.livros.client.ValidarTokenResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class JwtValidationFilter extends OncePerRequestFilter {

    // Mapeia: regex da rota -> métodos HTTP que exigem token nessa rota
    private static final Map<Pattern, String[]> ROTAS_PROTEGIDAS = Map.of(
            Pattern.compile("^/livros$"), new String[]{"POST"},
            Pattern.compile("^/livros/\\d+/estoque$"), new String[]{"PATCH"},
            Pattern.compile("^/livros/\\d+/emprestimo$"), new String[]{"POST"}
    );

    private final AuthServiceClient authServiceClient;

    public JwtValidationFilter(AuthServiceClient authServiceClient) {
        this.authServiceClient = authServiceClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!isRotaProtegida(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token não informado");
            return;
        }

        String token = authorization.substring("Bearer ".length());
        ValidarTokenResponse resultado = authServiceClient.validar(token);

        if (resultado == null || !resultado.valido()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
            return;
        }

        request.setAttribute("usuarioIdAutenticado", resultado.usuarioId());
        request.setAttribute("roleAutenticado", resultado.role());
        filterChain.doFilter(request, response);
    }

    private boolean isRotaProtegida(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String metodo = request.getMethod();

        return ROTAS_PROTEGIDAS.entrySet().stream()
                .anyMatch(entry -> entry.getKey().matcher(uri).matches()
                        && contemMetodo(entry.getValue(), metodo));
    }

    private boolean contemMetodo(String[] metodos, String metodo) {
        for (String m : metodos) {
            if (m.equals(metodo)) return true;
        }
        return false;
    }
}