# Deployment

## Render backend

1. Push this repository to GitHub.
2. In Render, create a new Blueprint from the repository. Render will read `render.yaml`.
3. Set `MONGO_URI` in Render environment variables.
4. After the service deploys, copy the backend URL, for example:

```text
https://offgrid-api.onrender.com
```

For cloud deployment, `P2P_ENABLED=false` and `MDNS_ENABLED=false` are intentional. Render exposes the HTTP app, but LAN mDNS and peer-to-peer TCP discovery are for local/offline network mode.

## Vercel frontend

1. Import the same repository in Vercel.
2. Set the root directory to `frontend`.
3. Build command:

```text
npm run build
```

4. Output directory:

```text
dist
```

5. Add environment variables:

```text
VITE_API_BASE_URL=https://offgrid-api.onrender.com
VITE_WS_BASE_URL=https://offgrid-api.onrender.com
```

6. In Render, update `CORS_ALLOWED_ORIGINS` with your real Vercel URL:

```text
https://your-project.vercel.app,https://*.vercel.app,http://localhost:5173
```

## Local LAN mesh mode

Use these env vars locally when you want libp2p plus mDNS:

```text
P2P_ENABLED=true
MDNS_ENABLED=true
TCP_PORT=9090
DISCOVERY_TAG=offgrid-demo
ROOM=general
NICK=Alpha
```
