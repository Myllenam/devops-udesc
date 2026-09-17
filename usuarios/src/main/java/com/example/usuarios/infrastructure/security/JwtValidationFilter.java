package com.example.usuarios.infrastructure.security;

import com.example.usuarios.infrastructure.client.AuthServiceClient;
import com.example.usuarios.infrastructure.client.ValidarTokenResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class JwtValidationFilter extends OncePerRequestFilter {

    private static final Pattern ROTA_PROTEGIDA = Pattern.compile("^/usuarios/\\d+$");
    private static final Set<String> METODOS_PROTEGIDOS = Set.of("GET", "PUT", "DELETE");

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
        return METODOS_PROTEGIDOS.contains(request.getMethod())
                && ROTA_PROTEGIDA.matcher(request.getRequestURI()).matches();
    }
}
