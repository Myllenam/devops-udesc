package com.example.auth.infrastructure.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MetricsAuthFilter extends OncePerRequestFilter {

    @Value("${METRICS_TOKEN:}")
    private String metricsToken;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"/actuator/prometheus".equals(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String recebido = request.getHeader("Authorization");
        String esperado = "Bearer " + metricsToken;

        boolean autorizado = !metricsToken.isBlank()
                && recebido != null
                && MessageDigest.isEqual(
                    recebido.getBytes(StandardCharsets.UTF_8),
                    esperado.getBytes(StandardCharsets.UTF_8)
                );

        if (!autorizado) {
            response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Acesso às métricas não autorizado"
            );
            return;
        }

        chain.doFilter(request, response);
    }
}
