package com.example.auth.infrastructure.client;

import com.example.auth.domain.exception.CadastroRecusadoException;
import com.example.auth.domain.exception.UsuarioServiceIndisponivelException;
import com.example.auth.infrastructure.client.dto.CriarUsuarioClientRequest;
import com.example.auth.infrastructure.client.dto.SituacaoClientResponse;
import com.example.auth.infrastructure.client.dto.UsuarioClientResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class UsuarioServiceClient {

    private final RestTemplate restTemplate;
    private final String usuariosServiceUrl;

    public UsuarioServiceClient(RestTemplate restTemplate,
                                 @Value("${usuarios.service.url}") String usuariosServiceUrl) {
        this.restTemplate = restTemplate;
        this.usuariosServiceUrl = usuariosServiceUrl;
    }

    public UsuarioClientResponse criarUsuario(CriarUsuarioClientRequest request) {
        try {
            return restTemplate.postForObject(usuariosServiceUrl + "/usuarios", request, UsuarioClientResponse.class);
        } catch (HttpClientErrorException ex) {
            throw new CadastroRecusadoException("Usuários Service recusou o cadastro: " + ex.getStatusText());
        } catch (RestClientException ex) {
            throw new UsuarioServiceIndisponivelException("Usuários Service indisponível");
        }
    }

    public SituacaoClientResponse consultarSituacao(String usuarioId) {
        try {
            return restTemplate.getForObject(
                    usuariosServiceUrl + "/usuarios/" + usuarioId + "/situacao",
                    SituacaoClientResponse.class);
        } catch (RestClientException ex) {
            throw new UsuarioServiceIndisponivelException("Usuários Service indisponível");
        }
    }
}
