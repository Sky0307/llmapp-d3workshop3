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
