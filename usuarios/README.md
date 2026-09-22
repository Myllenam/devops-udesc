# Usuarios Service

Microsserviço de cadastro e gestão de usuários (Spring Boot, Clean Architecture).

## Rodando localmente

```bash
docker compose up -d --build
```

Sobe o `usuarios` (porta 8082), seu banco (`usuarios-db`, porta 5433) e as dependências das quais ele depende para funcionar por completo: `auth` (porta 8083, usado pelo `JwtValidationFilter` para validar tokens) e `rabbitmq` (usado pelo `SagaCommandListener`).

## Testes

Os testes deste módulo são de dois tipos:

- **Testes de domínio** (`domain/entity/UsuarioTest`) e o `contextLoads` padrão — não precisam de nada rodando, `mvn test` já resolve.
- **Testes de integração** (`integration/UsuarioApiIntegrationTest`) — fazem chamadas HTTP reais contra os serviços `usuarios` e `auth` já rodando. **Exigem `docker compose up -d` de pé antes de rodar `mvn test`**, senão falham com "Connection refused".

Por padrão os testes de integração apontam para `http://localhost:8082` (usuarios) e `http://localhost:8083` (auth). Para rodá-los contra os nomes dos serviços na rede interna do Docker (por exemplo, para gerar cobertura executando os testes dentro de um container na mesma rede do `docker compose`), sobrescreva via variáveis de ambiente:

```bash
USUARIOS_URL=http://usuarios:8080 AUTH_URL=http://auth:8080 mvn test
```

