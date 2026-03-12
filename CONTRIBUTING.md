# Contributing

## Development Setup

### Prerequisites

- Java 21 (Eclipse Temurin recommended)
- Node.js 20+
- MongoDB 7+ (local or Docker)
- An OIDC provider for authentication testing

### Backend Development

1. Start MongoDB:
   ```bash
   docker run -d --name mongo -p 27017:27017 mongo:7-jammy
   ```

2. Run the Spring Boot app:
   ```bash
   cd backend
   ./gradlew bootRun
   ```

   The backend starts on `http://localhost:8080`.

### Frontend Development

1. Install dependencies:
   ```bash
   cd frontend
   npm install
   ```

2. Start the Vite dev server:
   ```bash
   npm run dev
   ```

   The frontend starts on `http://localhost:5173` and proxies API requests to the backend.

### Building for Production

The Docker build handles everything:

```bash
docker compose -f docker/docker-compose.yml build
```

This runs a multi-stage build:
1. Builds the Vue frontend
2. Builds the Spring Boot backend (incorporating the frontend assets)
3. Creates a minimal Alpine runtime image

### Code Style

- Java: Follow standard Spring Boot conventions
- TypeScript/Vue: Use Vue 3 Composition API with `<script setup>`
- Commit messages: Use conventional commits format (`feat:`, `fix:`, `docs:`, etc.)

### Testing

Run backend tests:
```bash
cd backend
./gradlew test
```

### Pull Requests

1. Create a feature branch from `main`
2. Make your changes with descriptive commits
3. Ensure tests pass
4. Submit a pull request with a clear description of changes
