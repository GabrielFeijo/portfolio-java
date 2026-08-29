# 🚀 Api-Portfolio-Java - Backend Spring Boot com MongoDB

> Backend Spring Boot com MongoDB para gerenciamento e fornecimento de dados para o portfólio interativo no estilo VS Code, incluindo comandos de terminal virtual, sistema de avaliações (reviews), formulário de contato, rate limiting granular com Bucket4j, arquitetura limpa (Hexagonal) e autenticação administrativa por API Key.

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange?logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0%20%2F%20Atlas-green?logo=mongodb)](https://www.mongodb.com/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203.0-brightgreen?logo=swagger)](https://swagger.io/)
[![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?logo=apachemaven)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue?logo=docker)](https://www.docker.com/)

> [!NOTE]
> **Aviso:** Esta API é um projeto demonstrativo de engenharia de software e **não reflete a API atualmente em produção** conectada ao portfólio oficial.

---

## 📋 Sobre o Projeto

O **Api-Portfolio-Java** é a implementação corporativa de alta performance desenvolvida para suportar o ecossistema do meu portfólio interativo ([VsCode-Portfolio](https://github.com/GabrielFeijo/VsCode-Portfolio)), acessível em [gabrielfeijo.com.br](https://gabrielfeijo.com.br).

Reconstruído em **Java 21 LTS** e **Spring Boot 3.3+**, o projeto preserva **100% de compatibilidade com os contratos de API anteriores**, agregando os padrões e recursos mais modernos da engenharia de software corporativa:

- **Arquitetura Limpa / Hexagonal (Ports & Adapters)** com separação estrita de Domínio, Aplicação e Infraestrutura.
- **Java 21 Virtual Threads (Project Loom)** ativadas nativamente para alta taxa de transferência e I/O não-bloqueante.
- **Persistência Nativa com Spring Data MongoDB**, garantindo total paridade com os documentos e coleções do Mongoose.
- **Segurança Robusta (Spring Security 6)** com autenticação administrativa via cabeçalho `x-api-key` / `Authorization: Bearer` e proteção HTTP Basic no Swagger.
- **Rate Limiting Granular** por IP de cliente utilizando algoritmo Token Bucket com **Bucket4j**.
- **Mapeamento Compile-Time Otimizado** com **MapStruct** e validação estrita com **Jakarta Bean Validation**.

---

## 🌐 Demonstração Local

| Serviço | URL | Descrição |
| --- | --- | --- |
| **API** | [http://localhost:3333/v2](http://localhost:3333/v2) | Prefixo principal da API (v2) |
| **Swagger** | [http://localhost:3333/swagger](http://localhost:3333/swagger) | Documentação OpenAPI interativa (protegida por Basic Auth) |
| **Comandos** | [http://localhost:3333/v2/command](http://localhost:3333/v2/command) | Catálogo de comandos para o terminal interativo |
| **Reviews** | [http://localhost:3333/v2/review](http://localhost:3333/v2/review) | Listagem e criação de depoimentos |
| **Contato** | [http://localhost:3333/v2/contact](http://localhost:3333/v2/contact) | Registro de mensagens de visitantes |
| **Health Check** | [http://localhost:3333/v2](http://localhost:3333/v2) | Status da API, latência do MongoDB e telemetria JVM |
| **Prometheus** | [http://localhost:3333/actuator/prometheus](http://localhost:3333/actuator/prometheus) | Métricas técnicas e telemetria da aplicação |
| **Insomnia** | [`Insomnia_Portfolio_Java_v2.json`](./Insomnia_Portfolio_Java_v2.json) | Collection completa pronta para importação no Insomnia |

---

## 🚀 Início Rápido

### Pré-requisitos

Certifique-se de ter instalado em sua máquina:

- [Java](https://adoptium.net/) 21 LTS
- [Docker](https://www.docker.com/get-started) e [Docker Compose](https://docs.docker.com/compose/)
- [MongoDB](https://www.mongodb.com/) local ou instância no [MongoDB Atlas](https://www.mongodb.com/atlas)

> O Maven 3.9 é obtido e gerenciado automaticamente pelo **Maven Wrapper (`./mvnw`)** incluído no repositório.

---

### Execução com Docker Compose (Modo Recomendado)

1. **Clone o repositório**

   ```bash
   git clone https://github.com/GabrielFeijo/portfolio-java.git
   cd portfolio-java
   ```

2. **Inicie o ecossistema completo (API + MongoDB)**

   ```bash
   docker compose up --build -d
   ```

3. **Acesse a documentação interativa**

   Abra [http://localhost:3333/swagger](http://localhost:3333/swagger) no navegador (credenciais: `admin` / `admin_password_here`).

---

### Execução Local com Maven Wrapper

1. **Configure as variáveis de ambiente**

   ```bash
   cp .env.example .env
   ```

   Edite o arquivo `.env` ajustando a connection string do MongoDB (`MONGO_URL`), credenciais do Swagger e sua `ADMIN_API_KEY`.

2. **Execute os testes automatizados**

   ```bash
   ./mvnw clean test
   ```

3. **Inicie a aplicação em modo desenvolvimento**

   ```bash
   ./mvnw spring-boot:run
   ```

---

## 📦 Estrutura do Projeto

```text
portfolio-java/
├── src/
│   ├── main/
│   │   ├── java/br/com/gabrielfeijo/portfolio/
│   │   │   ├── domain/                         # DOMAIN LAYER (Puro, sem acoplamento a frameworks)
│   │   │   │   ├── exception/                  # Exceções de domínio (ResourceNotFound, DuplicateResource)
│   │   │   │   ├── model/                      # Modelos de negócio (Command, Review, Contact)
│   │   │   │   └── repository/                 # Portas de persistência / Output Ports
│   │   │   │
│   │   │   ├── application/                    # APPLICATION LAYER (Casos de Uso & DTOs)
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/                # DTOs de entrada imutáveis com Bean Validation
│   │   │   │   │   └── response/               # DTOs de resposta padronizados com Jackson
│   │   │   │   ├── mapper/                     # MapStruct compile-time mappers
│   │   │   │   └── service/                    # Serviços de aplicação / Use cases
│   │   │   │
│   │   │   ├── infrastructure/                 # INFRASTRUCTURE LAYER
│   │   │   │   ├── configuration/              # Configurações Spring (Security, CORS, OpenAPI, Mongo)
│   │   │   │   ├── persistence/
│   │   │   │   │   ├── adapter/                # Implementação das portas com Spring Data MongoDB
│   │   │   │   │   ├── document/               # Documentos MongoDB (@Document, @Indexed)
│   │   │   │   │   ├── mapper/                 # Mapeadores Document <-> Domain
│   │   │   │   │   └── repository/             # Interfaces MongoRepository com queries Regex
│   │   │   │   ├── security/                   # Filtro de ApiKey, Token e RestEntryPoint
│   │   │   │   └── web/
│   │   │   │       ├── controller/             # REST Controllers (/v2/command, /v2/review, /v2/contact)
│   │   │   │       ├── filter/                 # RateLimitingFilter com Bucket4j
│   │   │   │       └── handler/                # GlobalExceptionHandler padronizado
│   │   │   │
│   │   │   └── PortfolioApplication.java       # Bootstrap da aplicação Spring Boot
│   │   │
│   │   └── resources/
│   │       ├── application.yml                 # Configuração base e Virtual Threads
│   │       ├── application-dev.yml             # Perfil de desenvolvimento
│   │       └── application-prod.yml            # Perfil de produção
│   │
│   └── test/                                   # Suíte de testes unitários, MockMvc e integração
│
├── .mvn/wrapper/                               # Binários do Maven Wrapper
├── mvnw / mvnw.cmd                             # Scripts de execução autônoma
├── Dockerfile                                  # Container multi-stage com JRE 21 Alpine non-root
├── docker-compose.yml                          # Orquestração da API + MongoDB
├── pom.xml                                     # Dependências e build Maven
└── README.md
```

---

## 🧱 Arquitetura e Fluxo de Requisições

```text
Frontend (VsCode-Portfolio) / Cliente HTTP
                │
                ▼
      ┌───────────────────┐
      │    CorsFilter     │ ──► Validação de Origens Permitidas (ORIGINS)
      └─────────┬─────────┘
                │
                ▼
      ┌───────────────────┐
      │RateLimitingFilter │ ──► Bucket4j Token Bucket (por IP de cliente e rota)
      └─────────┬─────────┘
                │
        ┌───────┴───────┐
        │               │
  [Rotas Públicas] [Rotas Administrativas]
        │               │
        │               ▼
        │        ┌──────────────┐
        │        │ ApiKeyFilter │ ──► Validação de 'x-api-key' ou 'Authorization: Bearer'
        │        └──────┬───────┘
        │               │
        ▼               ▼
 ┌─────────────────────────────┐
 │         Controllers         │ ──► Jakarta Bean Validation (@Valid) + OpenAPI 3
 └──────────────┬──────────────┘
                │
                ▼
 ┌─────────────────────────────┐
 │      Application Layer      │ ──► CommandService, ReviewService, ContactService + MapStruct
 └──────────────┬──────────────┘
                │
                ▼
 ┌─────────────────────────────┐
 │     Domain Layer (Ports)    │ ──► CommandRepositoryPort, ReviewRepositoryPort, ContactRepositoryPort
 └──────────────┬──────────────┘
                │
                ▼
 ┌─────────────────────────────┐
 │     Persistence Adapters    │ ──► CommandRepositoryAdapter, ReviewRepositoryAdapter
 └──────────────┬──────────────┘
                │
                ▼
 ┌─────────────────────────────┐
 │     Spring Data MongoDB     │ ──► MongoCommandRepository, MongoReviewRepository, MongoContactRepository
 └──────────────┬──────────────┘
                │
                ▼
 ┌─────────────────────────────┐
 │   MongoDB Database / Atlas  │ ──► Coleções: commands, reviews, contacts
 └─────────────────────────────┘
```

| Módulo | Responsabilidade |
| --- | --- |
| `command` | Gerencia e consulta comandos do terminal interativo (busca por nome/alias com sanitização NFD e CRUD administrativo). |
| `review` | Gerencia depoimentos/avaliações (estrelas 0-5, comentários), ordenação cronológica e moderação protegida. |
| `contact` | Recebe mensagens originadas pelo formulário de contato com validação rigorosa de email e texto. |
| `security` | Centraliza filtros de ApiKey, Basic Auth para Swagger, CORS e controle de acessos no Spring Security 6. |
| `web` | Contém controladores REST, filtro de rate limiting com Bucket4j e tratamento global de exceções. |

---

## 🔒 Segurança e Autenticação

- **Rotas Administrativas Protegidas**: As rotas de mutação sensíveis (`POST /v2/command`, `PUT /v2/command/:id`, `DELETE /v2/command/:id`, `PUT /v2/review/:id`, `DELETE /v2/review/:id`) exigem envio do cabeçalho `x-api-key: <ADMIN_API_KEY>` ou `Authorization: Bearer <ADMIN_API_KEY>`.
- **Rate Limiting Granular**: Proteção por IP de cliente com **Bucket4j** (2 req/10s para contato, 3 req/1s para reviews, 5 req/1s para comandos e 100 req/min geral).
- **Validação Estrita de Payload**: **Jakarta Bean Validation** rejeita automaticamente campos fora dos limites e formatações inválidas.
- **Documentação Protegida**: O acesso ao `/swagger` requer autenticação HTTP Basic (`SWAGGER_USER` / `SWAGGER_PASSWORD`).
- **Tratamento Padronizado de Erros**: Respostas de erro retornadas no padrão idêntico esperado pelo frontend (`statusCode`, `timestamp`, `path`, `method`, `message`, `error`).

---

## 🔧 Configuração de Ambiente

A aplicação carrega variáveis de ambiente do sistema ou do arquivo `.env`:

```env
PORT=3333
SPRING_PROFILES_ACTIVE=dev
ORIGINS="http://localhost:3000,http://localhost:5173,https://gabrielfeijo.com.br"

MONGO_URL="mongodb://localhost:27017/api-portfolio-v2"

SWAGGER_USER="admin"
SWAGGER_PASSWORD="admin_password_here"
SWAGGER_ENABLED=true

ADMIN_API_KEY="your_secure_admin_api_key_here"

DISCORD_WEBHOOK_URL="https://discord.com/api/webhooks/your/webhook/url"
DISCORD_WEBHOOK_ENABLED=true
```

| Variável | Padrão | Descrição |
| --- | --- | --- |
| `PORT` | `3333` | Porta HTTP em que o servidor irá escutar |
| `SPRING_PROFILES_ACTIVE` | `dev` | Perfil de execução ativo (`dev`, `prod`, `test`) |
| `ORIGINS` | `http://localhost:3000,...` | Lista de origens permitidas pelo CORS separadas por vírgula |
| `MONGO_URL` | `mongodb://localhost:27017/api-portfolio-v2` | String de conexão com o MongoDB local ou Atlas |
| `SWAGGER_USER` | `admin` | Usuário para login no Swagger via HTTP Basic Auth |
| `SWAGGER_PASSWORD` | `admin_password_here` | Senha para login no Swagger via HTTP Basic Auth |
| `SWAGGER_ENABLED` | `true` | Habilita ou desabilita a interface Swagger UI |
| `ADMIN_API_KEY` | `your_secure_admin_api_key_here` | Chave secreta de administrador necessária para rotas de mutação |
| `DISCORD_WEBHOOK_URL` | `""` | URL do Webhook do Discord para notificações em tempo real |
| `DISCORD_WEBHOOK_ENABLED` | `false` | Habilita ou desabilita o disparo assíncrono de notificações no Discord |

---

## 📝 Scripts Disponíveis

```bash
# Desenvolvimento & Execução
./mvnw spring-boot:run           # Inicia o servidor Spring Boot localmente
./mvnw compile                   # Compila o código fonte e gera mappers MapStruct

# Produção & Build
./mvnw clean package             # Compila e empacota o JAR de produção otimizado
java -jar target/portfolio-java-2.0.0.jar # Executa o binário JAR standalone

# Testes Automatizados
./mvnw test                      # Executa toda a suíte de testes de unidade e integração
./mvnw test -Dtest=CommandServiceTest # Executa um teste unitário específico

# Docker
docker compose up --build -d     # Constrói a imagem multi-stage e sobe com MongoDB
docker compose down              # Encerra os serviços e remove os containers
```

---
