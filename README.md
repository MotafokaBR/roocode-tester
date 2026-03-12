# Alias Forwarder - Addy.io to MXroute Sync

A Dockerized web application that reads email aliases from [Addy.io](https://addy.io) and creates matching email forwarders on [MXroute](https://mxroute.com). Built with Spring Boot 3 + Vue 3 + MongoDB, delivered as an Alpine-based container, compatible with Traefik, Caddy, and Nginx reverse proxies.

## Features

- Fetch aliases from Addy.io filtered by domain
- Create corresponding forwarders on MXroute
- Preview mode to see what will be synced before executing
- Idempotent sync -- skips forwarders that already exist
- Sync history with per-alias result tracking
- OIDC authentication (works with Keycloak, Authentik, or any OIDC provider)
- API keys are session-scoped only, never stored on disk

## Quick Start

### Prerequisites

- Docker and Docker Compose
- An OIDC identity provider (Keycloak, Authentik, etc.)
- Addy.io account with API key
- MXroute account with API key

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/MotafokaBR/roocode-tester.git
   cd roocode-tester
   ```

2. Copy and configure environment variables:
   ```bash
   cp docker/.env.example docker/.env
   # Edit docker/.env with your OIDC and other settings
   ```

3. Start the application:
   ```bash
   docker compose -f docker/docker-compose.yml up -d
   ```

4. Access the app at `http://localhost:8080`

### Reverse Proxy Setup

See [docs/reverse-proxy-examples.md](docs/reverse-proxy-examples.md) for Traefik, Caddy, and Nginx configuration examples.

## Architecture

| Layer | Technology |
|-------|-----------|
| Frontend | Vue 3 + Vite + TypeScript |
| Backend | Spring Boot 3 + Java 21 |
| Auth | Spring Security OIDC |
| Database | MongoDB |
| Container | Alpine Linux + Eclipse Temurin JRE 21 |
| Build | Gradle + npm |

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `MONGODB_URI` | `mongodb://localhost:27017/alias-forwarder` | MongoDB connection string |
| `OIDC_CLIENT_ID` | `alias-forwarder` | OIDC client ID |
| `OIDC_CLIENT_SECRET` | `change-me` | OIDC client secret |
| `OIDC_ISSUER_URI` | `https://auth.example.com/realms/main` | OIDC issuer URL |
| `MXROUTE_API_URL` | `https://mail.mxrouting.net` | MXroute API base URL |
| `SERVER_PORT` | `8080` | Application port |
| `LOG_LEVEL` | `INFO` | Application log level |

## API Endpoints

### Auth
- `GET /api/auth/login` - Get OIDC login URL
- `GET /api/auth/userinfo` - Current user info

### Sync Operations
- `POST /api/sync/preview` - Preview aliases that would be synced
- `POST /api/sync/execute` - Execute the sync operation
- `GET /api/sync/jobs` - List past sync jobs
- `GET /api/sync/jobs/{id}` - Get job details with per-alias logs

## Development

### Backend

```bash
cd backend
./gradlew bootRun
```

Requires Java 21 and a running MongoDB instance.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The Vite dev server proxies API requests to `localhost:8080`.

## Project Structure

```
roocode-tester/
  backend/                  Spring Boot application
    src/main/java/com/aliasforwarder/
      config/               Security, OIDC, WebClient config
      controller/           REST controllers
      service/              Sync engine, API clients
      model/                MongoDB documents
      repository/           Spring Data repos
      dto/                  Request/response DTOs
  frontend/                 Vue 3 SPA
    src/
      views/                Login, Dashboard, History views
      stores/               Pinia stores (auth, sync)
      api/                  API client
      router/               Vue Router with auth guards
  docker/                   Docker and compose files
  docs/                     Additional documentation
```

## License

MIT

## Disclaimer
This project was developed using AI assistance.
