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
