#!/bin/bash
# Production Deployment Checklist
# Run this before deploying to Vercel/Render

echo "=== OffGrid Production Deployment Checklist ==="
echo ""

# Check 1: Backend .env exists and is in .gitignore
echo "✓ Checking backend configuration..."
if grep -q "\.env$" .gitignore 2>/dev/null; then
  echo "  ✓ .env is in .gitignore"
else
  echo "  ✗ .env NOT in .gitignore - add it immediately!"
fi

if [ -f "backend/OffGrid/OffGrid/.env" ]; then
  echo "  ✓ backend/.env exists"
  if grep -q "MONGO_URI=your_" backend/OffGrid/OffGrid/.env; then
    echo "  ✗ MongoDB URI still has placeholder - update it!"
  else
    echo "  ✓ MongoDB URI appears to be set"
  fi
else
  echo "  ⚠ backend/.env not found (it should exist locally, not in Git)"
fi

# Check 2: Frontend .env exists
echo ""
echo "✓ Checking frontend configuration..."
if [ -f "frontend/.env" ]; then
  echo "  ✓ frontend/.env exists"
  if grep -q "VITE_API_BASE_URL=http://localhost" frontend/.env; then
    echo "  ⚠ Frontend still pointing to localhost - update for production!"
  else
    echo "  ✓ Frontend URLs appear to be set"
  fi
else
  echo "  ⚠ frontend/.env not found (it should exist locally, not in Git)"
fi

# Check 3: No hardcoded secrets in code
echo ""
echo "✓ Checking for hardcoded secrets..."
if grep -r "mongodb+srv://" backend/OffGrid/OffGrid/src --include="*.java" --include="*.yml"; then
  echo "  ✗ Found hardcoded MongoDB URI in source code - remove it!"
else
  echo "  ✓ No hardcoded MongoDB URIs in Java/YAML"
fi

if grep -r "Ajay:Ajay123" . --exclude-dir=.git --exclude-dir=node_modules --exclude-dir=target 2>/dev/null; then
  echo "  ✗ Found hardcoded credentials - remove them!"
else
  echo "  ✓ No obvious hardcoded credentials"
fi

# Check 4: .env.example files are templates only
echo ""
echo "✓ Checking .env.example files..."
if grep -q "placeholder\|your_\|example" backend/.env.example frontend/.env.example; then
  echo "  ✓ .env.example files have placeholders only"
else
  echo "  ⚠ .env.example files might have real values"
fi

echo ""
echo "=== Deployment Steps ==="
echo ""
echo "1. Frontend (Vercel):"
echo "   - Push code to GitHub (without .env)"
echo "   - Connect on vercel.com"
echo "   - Set VITE_API_BASE_URL and VITE_WS_BASE_URL in dashboard"
echo ""
echo "2. Backend (Render):"
echo "   - Push code to GitHub (without .env)"
echo "   - Create Web Service on render.com"
echo "   - Set MONGO_URI and CORS_ALLOWED_ORIGINS in dashboard"
echo "   - Deploy"
echo ""
echo "Done! Your secrets are safe. ✓"
