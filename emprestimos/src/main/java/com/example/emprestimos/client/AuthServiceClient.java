package com.example.emprestimos.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class AuthServiceClient {

    private final RestTemplate restTemplate;
    private final String authServiceUrl;

    public AuthServiceClient(RestTemplate restTemplate,
            @Value("${auth.service.url}") String authServiceUrl) {
        this.restTemplate = restTemplate;
        this.authServiceUrl = authServiceUrl;
    }

    public ValidarTokenResponse validar(String token) {
        try {
            return restTemplate.postForObject(
                    authServiceUrl + "/auth/validate",
                    Map.of("token", token),
                    ValidarTokenResponse.class);
        } catch (RestClientException ex) {
            return new ValidarTokenResponse(false, null, null, "Falha ao contatar o serviço de autenticação");
        }
    }
}
