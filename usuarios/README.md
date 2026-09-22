# Usuarios Service

Microsserviço de cadastro e gestão de usuários (Spring Boot, Clean Architecture).

## Rodando localmente

```bash
docker compose up -d --build
```

Sobe o `usuarios` (porta 8082), seu banco (`usuarios-db`, porta 5433) e as dependências das quais ele depende para funcionar por completo: `auth` (porta 8083, usado pelo `JwtValidationFilter` para validar tokens) e `rabbitmq` (usado pelo `SagaCommandListener`).

## Testes

- **Testes de domínio** (`domain/entity/UsuarioTest`) — unitários puros, sem Spring, sem banco, sem mock.
- **Testes de use case** (`application/usecase/*Test`) — unitários com Mockito, mockando o repositório e os clients externos.
- **Teste de controller** (`presentation/controller/UsuarioControllerTest`) — slice `@WebMvcTest`, com os use cases mockados via `@MockitoBean`.
- **`UsuariosApplicationTests.contextLoads`** — sobe o contexto Spring completo e precisa de um Postgres real ouvindo em `localhost:5433` (ou seja, precisa do `docker compose up -d` de pé, ao menos para o `usuarios-db`).

`mvn test` roda tudo isso normalmente, exceto o `contextLoads`, que só passa com o `docker compose` de pé.

