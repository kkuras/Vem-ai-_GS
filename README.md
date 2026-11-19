# Vem Aí · API

API REST em Java do **Vem Aí**, projeto da **Global Solution 2026 · 2º semestre (CitizenTech)**, disciplina de **Domain Driven Design Using Java** da FIAP, turma **1TDSPJ**.

O Vem Aí avisa o cidadão só quando o entregador está a poucas paradas da casa dele, por um link que abre no navegador, sem instalar app. Esta API guarda os dados no Oracle da FIAP e atende o front-end do grupo.

| | Link |
|---|---|
| ☕ API publicada (Render) | https://vem-ai-gs.onrender.com |
| 🌐 Front-end (Vercel) | https://vem-ai-zeta.vercel.app |
| 🎬 Vídeo (YouTube) | https://youtu.be/-petAyehKTw |

> A API está no plano gratuito do Render, que hiberna sem uso. A primeira chamada pode levar até 1 minuto.

## Integrantes

| Nome | RM | Turma |
|---|---|---|
| Tiago Borges Dos Santos | 569926 | 1TDSPJ |
| Flávio Luiz Kuratomi Junior | 571211 | 1TDSPJ |
| João Victor de Jesus Bernardo | 568729 | 1TDSPJ |
| Pedro Andreotti Pugliesi | 569357 | 1TDSPJ |
| Henrique Osuka | 571324 | 1TDSPJ |

## Tecnologias

- Java 21 e Spring Boot 4.1 (Web MVC e Bean Validation)
- JDBC com `PreparedStatement` (sem JPA, Hibernate ou Spring Data)
- Oracle Database (FIAP)
- Docker (build multi-stage) e Render para o deploy

## Como funciona

1. No centro de distribuição, o operador **bipa** as encomendas. A API coloca cada uma no lote ("pacotão") do entregador que atende a região do cliente.
2. O entregador lê o QR do lote e **inicia a rota**: todas as encomendas passam para `EM_ROTA` e a API calcula a ordem das paradas, sempre indo para a mais próxima.
3. Durante a rota, o front envia a **localização** do entregador.
4. A cada **entrega concluída**, a API recalcula quantas paradas faltam para cada cliente e registra o evento de aviso (`AVISO_ENVIADO`) para quem acabou de chegar a 3 paradas.
5. O cidadão consulta o **rastreio** pelo código. O mapa com o entregador só é liberado quando faltam 3 paradas ou menos.

## Estrutura de pacotes

| Pacote | Responsabilidade |
|---|---|
| `models` | Entidades do domínio (Lote, Encomenda, EventoRastreio, Entregador, Cliente, CentroDistribuicao, Regiao) |
| `dao` | Acesso ao Oracle com JDBC, um DAO por entidade |
| `dto` | Dados de entrada e de saída da API, com Bean Validation |
| `mapper` | Conversão entre DTOs e models |
| `service` | Regras de negócio |
| `controller` | Rotas REST e status HTTP |
| `exception` | Exceções do domínio e montagem do JSON de erro |
| `handler` | JSON de erro padronizado para erros fora dos controllers (JSON inválido, rota ou método inexistente) |
| `config` | Configuração do CORS |
| `enums` | Status de lote, encomenda e entregador, tipos de evento e de usuário |

## Endpoints

| Recurso | Rotas |
|---|---|
| Lotes | `POST /lotes` · `GET /lotes` · `GET /lotes/{id}` · `PUT /lotes/{id}` · `DELETE /lotes/{id}` · `GET /lotes/{id}/encomendas` · `POST /lotes/iniciar-rota` · `PUT /lotes/{id}/localizacao` |
| Encomendas | `POST /encomendas` (bipar) · `GET /encomendas` · `GET /encomendas/{id}` · `PUT /encomendas/{id}` · `DELETE /encomendas/{id}` · `POST /encomendas/{id}/entrega` · `GET /encomendas/{id}/eventos` · `POST /encomendas/{id}/eventos` |
| Eventos de rastreio | `GET /eventos` · `GET /eventos/{id}` · `PUT /eventos/{id}` · `DELETE /eventos/{id}` |
| Entregadores | `POST /entregadores` · `GET /entregadores` · `GET /entregadores/{id}` · `PUT /entregadores/{id}` · `DELETE /entregadores/{id}` · `PUT /entregadores/{id}/disponibilidade` · `GET /entregadores/{id}/lote-atual` |
| Clientes | `POST /clientes` · `GET /clientes` · `GET /clientes/{id}` · `PUT /clientes/{id}` · `DELETE /clientes/{id}` · `GET /clientes/{id}/encomendas` |
| Centros de distribuição | `POST /centros` · `GET /centros` · `GET /centros/{id}` · `PUT /centros/{id}` · `DELETE /centros/{id}` |
| Regiões | `POST /regioes` · `GET /regioes` · `GET /regioes/{id}` · `PUT /regioes/{id}` · `DELETE /regioes/{id}` |
| Rastreio público | `GET /rastreio/{codigo}` |
| Login | `POST /login` |

### Status HTTP

| Status | Quando |
|---|---|
| `200` | Consulta ou atualização com sucesso |
| `201` + `Location` | Cadastro com sucesso |
| `204` | Remoção com sucesso |
| `400` | Campos inválidos, JSON mal formatado ou parâmetro inválido na URL |
| `401` | E-mail ou senha incorretos no login |
| `403` | Entregador tentando iniciar a rota de um lote que não é dele |
| `404` | Registro ou rota inexistente |
| `405` | Método HTTP não suportado na rota |
| `409` | Regra de negócio violada ou remoção de registro com vínculos |
| `500` | Erro inesperado (mensagem genérica, sem detalhes internos) |

Todo erro volta no mesmo formato:

```json
{
  "status": 400,
  "erro": "Validação",
  "mensagem": "Campos inválidos",
  "campos": { "cdCentro": "O centro é obrigatório" }
}
```

### Regras de negócio (respondem 409)

- Um lote comporta no máximo **20 encomendas**. Sem entregador com vaga na região, a encomenda não é bipada.
- A rota não pode começar num lote **vazio** nem num lote **já iniciado ou concluído**.
- Só é possível concluir a entrega de uma encomenda que está **em rota**.
- Um entregador com rota em andamento não pode ficar indisponível.
- O e-mail de cliente e de entregador é único.

## Como rodar

### Variáveis de ambiente

| Variável | Para quê |
|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC do Oracle |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `CORS_ORIGENS` | Opcional. Domínios do front liberados no CORS, separados por vírgula (padrão: o front na Vercel e `http://localhost:5173`) |
| `PORT` | Opcional. Porta da API (padrão: `8081`) |

Localmente, copie `.env.exemple` para `.env` e preencha os valores. O `.env` não vai para o repositório.

### Banco de dados

Rode o script `vem ai tabelas.sql` no Oracle para criar as tabelas e as sequences.

### Com Docker

```bash
docker build -t vem-ai-api .
docker run -p 8081:8080 -e PORT=8080 \
  -e SPRING_DATASOURCE_URL=... \
  -e SPRING_DATASOURCE_USERNAME=... \
  -e SPRING_DATASOURCE_PASSWORD=... \
  vem-ai-api
```

### Com Maven (JDK 21)

```bash
./mvnw spring-boot:run
```

A API sobe em http://localhost:8081.

## Deploy

A API é publicada no Render a partir do `Dockerfile` (build multi-stage: Maven compila, e a imagem final leva só o `.jar` com o JRE 21). As credenciais do banco ficam nas variáveis de ambiente do Render, nunca no código.
