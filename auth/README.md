# Auth Service

Microsserviço de autenticação (registro, login e validação de token JWT), Spring Boot, Clean Architecture.

## Rodando localmente

```bash
docker compose up -d --build
```

Sobe o `auth` (porta 8083), seu banco (`auth-db`, porta 5434) e o `usuarios` (porta 8082), do qual ele depende: `/auth/register` cria o usuário via `POST /usuarios` e `/auth/login` consulta a situação dele via `GET /usuarios/{id}/situacao`.

## Testes

- **`infrastructure/security/JwtServiceTest`** — unitário puro, sem Spring, sem banco, sem mock.
- **Testes de use case** (`application/usecase/*Test`) — unitários com Mockito, mockando o repositório e os clients externos.
- **Teste de controller** (`presentation/controller/AuthControllerTest`) — slice `@WebMvcTest`, com os use cases mockados via `@MockitoBean`.
- **`AuthApplicationTests.contextLoads`** — sobe o contexto Spring completo e precisa de um Postgres real ouvindo em `localhost:5434` (ou seja, precisa do `docker compose up -d` de pé, ao menos para o `auth-db`).

`mvn test` roda tudo isso normalmente, exceto o `contextLoads`, que só passa com o `docker compose` de pé.

