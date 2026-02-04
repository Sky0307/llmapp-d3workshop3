# Dockerize llm-multiroute and llm-frontend-python

## Overview

Create Dockerfiles for both projects and a `docker-compose.yml` at the Day3 level to orchestrate them together on a shared network. The API key is kept out of all committed files and loaded at runtime via a `.env` file.

---

## Step-by-Step Plan

### Step 1: Create `llm-multiroute/Dockerfile` (Backend API)

The backend is a **FastAPI** app with **per-task model routing**, served by **uvicorn** on port **8080**. Each analysis type (classify, sentiment, summarize, intent) can use a different LLM model.

**Dockerfile details:**

| Setting | Value |
|---------|-------|
| Base image | `python:3.12-slim` |
| Working dir | `/app` |
| Dependencies | Install from `requirements.txt` |
| App code | Copy `app/` directory |
| Port | Expose `8080` |
| Startup command | `uvicorn app.main:app --host 0.0.0.0 --port 8080` |

**Environment variables (with defaults):**

| Variable | Default | Purpose |
|----------|---------|---------|
| `SERVER_PORT` | `8080` | Port the API listens on |
| `OLLAMA_BASE_URL` | `https://ollama.com` | Ollama API endpoint |
| `OLLAMA_TEMPERATURE` | `0.7` | Model temperature |
| `OLLAMA_API_KEY` | *(loaded from `.env` file)* | Bearer token for Ollama cloud |
| `OLLAMA_MODEL_CLASSIFY` | `gemma3:4b` | Model for text classification |
| `OLLAMA_MODEL_SENTIMENT` | `ministral-3:3b` | Model for sentiment analysis |
| `OLLAMA_MODEL_SUMMARIZE` | `ministral-3:8b` | Model for text summarization |
| `OLLAMA_MODEL_INTENT` | `gemma3:12b` | Model for intent detection |

**Dockerfile content:**
```dockerfile
FROM python:3.12-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app/ ./app/

ENV SERVER_PORT=8080
ENV OLLAMA_BASE_URL=https://ollama.com
ENV OLLAMA_TEMPERATURE=0.7
ENV OLLAMA_MODEL_CLASSIFY=gemma3:4b
ENV OLLAMA_MODEL_SENTIMENT=ministral-3:3b
ENV OLLAMA_MODEL_SUMMARIZE=ministral-3:8b
ENV OLLAMA_MODEL_INTENT=gemma3:12b

EXPOSE 8080

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8080"]
```

---

### Step 2: Create `llm-multiroute/.dockerignore`

Prevents unnecessary files from being included in the Docker build context.

```
__pycache__
*.pyc
.env
tests/
.git
.venv
.pytest_cache
readme.md
```

---

### Step 3: Create `llm-frontend-python/Dockerfile` (Frontend UI)

The frontend is a **Flask** app that serves HTML/CSS/JS and proxies API requests to the backend. Uses **gunicorn** as a production WSGI server.

**Why gunicorn over Flask dev server:**
- Flask's built-in server is single-threaded and handles one request at a time
- Gunicorn runs multiple worker processes for concurrent request handling
- Flask dev server has debug features that are security risks in production
- Gunicorn is the industry standard for Python web apps in containers

**Dockerfile details:**

| Setting | Value |
|---------|-------|
| Base image | `python:3.12-slim` |
| Working dir | `/app` |
| Dependencies | Install from `requirements.txt` + `gunicorn` |
| App code | Copy `app.py`, `config.py`, `templates/`, `static/` |
| Port | Expose `5000` |
| Startup command | `gunicorn --bind 0.0.0.0:5000 --workers 2 app:app` |

**Environment variables (with defaults):**

| Variable | Default | Purpose |
|----------|---------|---------|
| `BACKEND_URL` | `http://llm-backend:8080` | URL of the backend API (uses Docker service name) |
| `FLASK_PORT` | `5000` | Port the frontend listens on |
| `FLASK_DEBUG` | `false` | Debug mode (disabled for production) |

**Dockerfile content:**
```dockerfile
FROM python:3.12-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt gunicorn

COPY app.py config.py ./
COPY templates/ ./templates/
COPY static/ ./static/

ENV BACKEND_URL=http://llm-backend:8080
ENV FLASK_PORT=5000
ENV FLASK_DEBUG=false

EXPOSE 5000

CMD ["gunicorn", "--bind", "0.0.0.0:5000", "--workers", "2", "app:app"]
```

---

### Step 4: Create `llm-frontend-python/.dockerignore`

```
__pycache__
*.pyc
.env
.git
.venv
README.md
```

---

### Step 5: Create `docker-compose.yml` (at Day3 root)

Orchestrates both services on a shared Docker network. The frontend references the backend by its Docker service name `llm-backend`. The `OLLAMA_API_KEY` is loaded from the `.env` file via `env_file` — it never appears in docker-compose.yml or any Dockerfile.

**docker-compose.yml content:**
```yaml
services:
  llm-backend:
    build: ./llm-multiroute
    container_name: llm-backend
    ports:
      - "8080:8080"
    env_file:
      - .env
    environment:
      - OLLAMA_BASE_URL=${OLLAMA_BASE_URL:-https://ollama.com}
      - OLLAMA_TEMPERATURE=${OLLAMA_TEMPERATURE:-0.7}
      - OLLAMA_MODEL_CLASSIFY=${OLLAMA_MODEL_CLASSIFY:-gemma3:4b}
      - OLLAMA_MODEL_SENTIMENT=${OLLAMA_MODEL_SENTIMENT:-ministral-3:3b}
      - OLLAMA_MODEL_SUMMARIZE=${OLLAMA_MODEL_SUMMARIZE:-ministral-3:8b}
      - OLLAMA_MODEL_INTENT=${OLLAMA_MODEL_INTENT:-gemma3:12b}

  llm-frontend:
    build: ./llm-frontend-python
    container_name: llm-frontend
    ports:
      - "5000:5000"
    environment:
      - BACKEND_URL=http://llm-backend:8080
    depends_on:
      - llm-backend
```

**Key design decisions:**
- `env_file: .env` loads `OLLAMA_API_KEY` at runtime (kept out of version control)
- `depends_on` ensures the backend starts before the frontend
- `BACKEND_URL=http://llm-backend:8080` uses Docker's internal DNS (service name resolution)
- Per-task model variables can be overridden from the host environment or `.env` file
- Both services are on Docker Compose's default shared network

---

### Step 6: Create `.env.example` (at Day3 root)

A safe-to-commit template showing required environment variables without actual secrets.

```
# Ollama API Configuration
OLLAMA_API_KEY=your_api_key_here
OLLAMA_BASE_URL=https://ollama.com
OLLAMA_TEMPERATURE=0.7

# Per-task model routing
OLLAMA_MODEL_CLASSIFY=gemma3:4b
OLLAMA_MODEL_SENTIMENT=ministral-3:3b
OLLAMA_MODEL_SUMMARIZE=ministral-3:8b
OLLAMA_MODEL_INTENT=gemma3:12b
```

---

### Step 7: Create `.gitignore` (at Day3 root)

Ensures `.env` and other sensitive/generated files are never committed.

```
.env
__pycache__/
*.pyc
.venv/
.idea/
.pytest_cache/
```

---

## Files Summary

| # | File | Location | Purpose |
|---|------|----------|---------|
| 1 | `Dockerfile` | `llm-multiroute/` | Backend FastAPI container (multi-route) |
| 2 | `.dockerignore` | `llm-multiroute/` | Exclude unnecessary files from backend build |
| 3 | `Dockerfile` | `llm-frontend-python/` | Frontend Flask + gunicorn container |
| 4 | `.dockerignore` | `llm-frontend-python/` | Exclude unnecessary files from frontend build |
| 5 | `docker-compose.yml` | `Day3/` (root) | Orchestrate both services |
| 6 | `.env.example` | `Day3/` (root) | Template for environment variables (safe to commit) |
| 7 | `.gitignore` | `Day3/` (root) | Prevent `.env` and generated files from being committed |

---

## Verification

After implementation, verify with:

1. **Setup**: `cp .env.example .env` then fill in your `OLLAMA_API_KEY`
2. **Build**: `docker compose build` — both images should build without errors
3. **Run**: `docker compose up` — both services should start and show logs
4. **Frontend**: Open `http://localhost:5000` in browser — UI should load
5. **Backend**: `curl http://localhost:8080/swagger-ui.html` — Swagger UI should respond
6. **Routing**: `curl http://localhost:8080/api/ai/routes` — should show per-task model assignments
7. **End-to-end**: Enter text in frontend, run an analysis — frontend should proxy to backend and display results