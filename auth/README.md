# Auth Service

Microsserviço de autenticação (registro, login e validação de token JWT), Spring Boot, Clean Architecture.

## Rodando localmente

```bash
docker compose up -d --build
```

Sobe o `auth` (porta 8083), seu banco (`auth-db`, porta 5434) e o `usuarios` (porta 8082), do qual ele depende: `/auth/register` cria o usuário via `POST /usuarios` e `/auth/login` consulta a situação dele via `GET /usuarios/{id}/situacao`.

## Testes

- **`infrastructure/security/JwtServiceTest`** e o `contextLoads` padrão — não precisam de nada rodando, `mvn test` já resolve.
- **`integration/AuthApiIntegrationTest`** — chamadas HTTP reais contra `auth` e `usuarios` já rodando. **Exige `docker compose up -d` de pé antes de rodar `mvn test`**, senão falha com "Connection refused".

Por padrão aponta para `http://localhost:8083` (auth). Para rodar contra os nomes dos serviços na rede interna do Docker:

```bash
AUTH_URL=http://auth:8080 USUARIOS_URL=http://usuarios:8080 mvn test
```

