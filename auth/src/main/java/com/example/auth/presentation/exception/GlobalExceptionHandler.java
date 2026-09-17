package com.example.auth.presentation.exception;

import com.example.auth.domain.exception.CadastroRecusadoException;
import com.example.auth.domain.exception.CredenciaisInvalidasException;
import com.example.auth.domain.exception.EmailJaCadastradoException;
import com.example.auth.domain.exception.TokenInvalidoException;
import com.example.auth.domain.exception.UsuarioInativoOuBloqueadoException;
import com.example.auth.domain.exception.UsuarioServiceIndisponivelException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EmailJaCadastradoException.class, CadastroRecusadoException.class})
    public ResponseEntity<Map<String, Object>> handleConflito(RuntimeException ex) {
        return montarResposta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({CredenciaisInvalidasException.class, TokenInvalidoException.class})
    public ResponseEntity<Map<String, Object>> handleNaoAutorizado(RuntimeException ex) {
        return montarResposta(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(UsuarioInativoOuBloqueadoException.class)
    public ResponseEntity<Map<String, Object>> handleProibido(UsuarioInativoOuBloqueadoException ex) {
        return montarResposta(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(UsuarioServiceIndisponivelException.class)
    public ResponseEntity<Map<String, Object>> handleServicoIndisponivel(UsuarioServiceIndisponivelException ex) {
        return montarResposta(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(erro -> erros.put(erro.getField(), erro.getDefaultMessage()));
        body.put("erros", erros);
        return ResponseEntity.badRequest().body(body);
    }

    private ResponseEntity<Map<String, Object>> montarResposta(HttpStatus status, String mensagem) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("mensagem", mensagem);
        return ResponseEntity.status(status).body(body);
    }
}
