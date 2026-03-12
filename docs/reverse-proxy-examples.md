# Reverse Proxy Configuration Examples

The Alias Forwarder container exposes HTTP on port 8080. TLS termination should be handled by your reverse proxy.

## Traefik

Use the Traefik compose override:

```bash
docker compose -f docker/docker-compose.yml -f docker/docker-compose.traefik.yml up -d
```

This adds Traefik labels for automatic service discovery. Make sure your Traefik instance is on the `traefik` Docker network.

The labels configure:
- Host-based routing to `alias.example.com` (set via `APP_DOMAIN` env var)
- Automatic TLS via Let's Encrypt (`letsencrypt` cert resolver)
- Load balancer targeting port 8080

## Caddy

Use the Caddy compose override:

```bash
docker compose -f docker/docker-compose.yml -f docker/docker-compose.caddy.yml up -d
```

Add this to your Caddyfile:

```
alias.example.com {
    reverse_proxy alias-forwarder:8080
}
```

Caddy handles TLS automatically.

## Nginx

Use the Nginx compose override:

```bash
docker compose -f docker/docker-compose.yml -f docker/docker-compose.nginx.yml up -d
```

Add this server block to your nginx configuration:

```nginx
server {
    listen 443 ssl;
    server_name alias.example.com;

    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;

    location / {
        proxy_pass http://alias-forwarder:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## Direct Access (Development)

For local development without a reverse proxy:

```bash
docker compose -f docker/docker-compose.yml up -d
```

The app will be available at `http://localhost:8080`.
