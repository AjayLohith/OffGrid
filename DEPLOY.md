# OffGrid - Deployment Guide

## Environment Setup

Both backend and frontend use environment variables for configuration. **Never commit `.env` files to Git.**

### Backend Setup

1. **Copy the template:**
   ```bash
   cd backend/OffGrid/OffGrid
   cp .env.example .env
   ```

2. **Update `.env` with your values:**
   ```bash
   # Edit .env and set these required values:
   MONGO_URI=your_mongodb_atlas_connection_string
   CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:5174,http://localhost:3000
   ```

3. **Load environment and run:**
   ```bash
   # On Windows (PowerShell):
   $env:PORT=8080; $env:TCP_PORT=9090; $env:MONGO_URI='your_uri_here'; .\mvnw.cmd spring-boot:run
   
   # On macOS/Linux:
   export $(cat .env | xargs) && ./mvnw spring-boot:run
   ```

### Frontend Setup

1. **Copy the template:**
   ```bash
   cd frontend
   cp .env.example .env
   ```

2. **Update `.env` with your backend URL:**
   ```bash
   # Local development:
   VITE_API_BASE_URL=http://localhost:8080
   VITE_WS_BASE_URL=http://localhost:8080
   
   # Production (Render):
   VITE_API_BASE_URL=https://your-backend.onrender.com
   VITE_WS_BASE_URL=https://your-backend.onrender.com
   ```

3. **Run dev server:**
   ```bash
   npm install
   npm run dev
   ```

---

## Production Deployment

### Vercel (Frontend)

1. **Push to GitHub** (with `.env` in `.gitignore`)
   ```bash
   git add .
   git commit -m "Setup environment configuration"
   git push origin main
   ```

2. **Connect to Vercel:**
   - Go to https://vercel.com/new
   - Import your GitHub repository
   - Set build command: `npm install && npm run build`
   - Set output directory: `dist`

3. **Add Environment Variables in Vercel Dashboard:**
   ```
   VITE_API_BASE_URL=https://your-backend.onrender.com
   VITE_WS_BASE_URL=https://your-backend.onrender.com
   ```

4. **Deploy** (Vercel will build and deploy automatically)

### Render (Backend)

1. **Create Docker configuration** (optional, but recommended):
   - Render auto-detects Maven projects
   - Or use the provided `Dockerfile`

2. **Push to GitHub** (`.env` stays local, excluded by `.gitignore`)

3. **Create a new Web Service on Render:**
   - Go to https://dashboard.render.com
   - Click "New" → "Web Service"
   - Select your GitHub repository
   - Choose "Docker" runtime (or let Render detect Maven)

4. **Configure in Render Dashboard:**
   - **Name:** offgrid-backend
   - **Build command:** `./mvnw -DskipTests package` (if not using Docker)
   - **Start command:** `java -jar target/*.jar` (if not using Docker)
   - **Environment Variables:**
     ```
     PORT=8080
     MONGO_URI=your_mongodb_atlas_connection_string
     CORS_ALLOWED_ORIGINS=https://your-frontend.vercel.app,https://your-other-domain.com
     NICK=cloud-node
     ROOM=general
     ```

5. **Deploy** (Render will build and deploy automatically)

---

## Environment Variables Reference

### Backend (`.env` or Render dashboard)

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `PORT` | No | 8080 | Server port |
| `TCP_PORT` | No | 9090 | P2P mesh TCP port |
| `MONGO_URI` | **Yes** | — | MongoDB Atlas connection string |
| `CORS_ALLOWED_ORIGINS` | No | localhost | Comma-separated allowed origins |
| `NICK` | No | anonymous | Node display name |
| `ROOM` | No | general | Default chat room |
| `MDNS_ENABLED` | No | true | Enable mDNS discovery (disable on cloud) |
| `P2P_ENABLED` | No | true | Enable P2P mesh (disable on cloud) |
| `SEED_PEERS` | No | — | Comma-separated seed peer addresses |

### Frontend (`.env` or Vercel dashboard)

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `VITE_API_BASE_URL` | No | http://localhost:8080 | Backend REST API URL |
| `VITE_WS_BASE_URL` | No | http://localhost:8080 | Backend WebSocket URL |

---

## Local Development

```bash
# Terminal 1 - Backend
cd backend/OffGrid/OffGrid
cp .env.example .env
# Edit .env with MongoDB URI
$env:PORT=8080; $env:TCP_PORT=9090; $env:MONGO_URI='your_uri'; .\mvnw.cmd spring-boot:run

# Terminal 2 - Frontend
cd frontend
cp .env.example .env
npm install
npm run dev
```

Visit: `http://localhost:5174`

---

## Troubleshooting

### "MONGO_URI is required"
- **Cause:** Environment variable not set
- **Fix:** Add `MONGO_URI` to `.env` (backend) or Render/Vercel dashboard

### "CORS policy blocked"
- **Cause:** Frontend URL not in `CORS_ALLOWED_ORIGINS`
- **Fix:** Update backend CORS config with your frontend domain

### "Connection refused"
- **Cause:** Backend not running or URL incorrect
- **Fix:** Check `VITE_API_BASE_URL` matches backend address

### P2P mesh not discovering peers
- **Cause:** mDNS doesn't work across cloud infrastructure
- **Fix:** This is expected - P2P works only on local LAN. Cloud deploy is REST/WebSocket only.

---

## Security Checklist

- ✅ `.env` files are in `.gitignore` (not committed)
- ✅ `.env.example` templates show placeholders only
- ✅ No hardcoded credentials in code or YAML
- ✅ Secrets set via environment (local `.env` or cloud provider dashboard)
- ✅ MongoDB credentials in `MONGO_URI` only
- ✅ CORS restricted to known origins

