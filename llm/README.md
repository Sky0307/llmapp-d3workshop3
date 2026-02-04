# Spring Boot AI with Ollama Integration

A Spring Boot application that integrates with Ollama to provide AI-powered text analysis APIs including classification, sentiment analysis, summarization, and intent detection.

## Prerequisites

- Java 17 or higher
- Ollama installed and running locally
- Llama model pulled in Ollama (e.g., `ollama pull llama3.2`)

## Project Structure

```
llm/
├── src/
│   └── main/
│       ├── java/com/example/llm/
│       │   ├── LlmSpringAiApplication.java
│       │   ├── controller/
│       │   │   └── AIController.java
│       │   ├── service/
│       │   │   └── AIService.java
│       │   └── dto/
│       │       ├── TextRequest.java
│       │       ├── ClassificationResponse.java
│       │       ├── SentimentResponse.java
│       │       ├── SummaryResponse.java
│       │       └── IntentResponse.java
│       └── resources/
│           └── application.yml
├── pom.xml
├── mvnw
└── mvnw.cmd
```

## Configuration

The application is configured in `src/main/resources/application.yml`:

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama3.2
          temperature: 0.7
```

You can modify:
- `base-url`: Change if Ollama is running on a different host/port
- `model`: Use a different Ollama model
  - **Recommended:** `llama3.2:3b`, `llama3.1:8b`, or `mistral:7b` for reliable JSON output
  - Small models like `smollm2:360m` may return null values due to poor JSON generation
- `temperature`: Adjust creativity (0.0 = deterministic, 1.0 = creative)

## Build and Run

### Using Maven Wrapper (Recommended)

**On Unix/Linux/Mac:**
```bash
./mvnw clean install
./mvnw spring-boot:run
```

**On Windows:**
```cmd
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

### Using Maven (if installed)

```bash
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Swagger UI (API Testing Interface)

Once the application is running, you can access the Swagger UI to test all APIs interactively:

**Swagger UI:** http://localhost:8080/swagger-ui.html

**OpenAPI Documentation:** http://localhost:8080/api-docs

The Swagger UI provides:
- Interactive API testing without needing cURL or Postman
- Complete API documentation with request/response schemas
- Try it out feature to execute API calls directly from the browser
- Example request/response bodies for all endpoints

Simply navigate to the Swagger UI URL, select an endpoint, click "Try it out", enter your text, and click "Execute" to see the results.

## API Endpoints

All endpoints accept POST requests with JSON body containing a `text` field.

### 1. Text Classification

**Endpoint:** `POST /api/ai/classify`

**Request:**
```json
{
  "text": "Apple announced a new iPhone with advanced AI capabilities and improved camera features."
}
```

**Response:**
```json
{
  "labels": ["technology", "product announcement", "smartphones"],
  "primaryCategory": "technology news",
  "confidence": 0.95
}
```

### 2. Sentiment Analysis

**Endpoint:** `POST /api/ai/sentiment`

**Request:**
```json
{
  "text": "I absolutely love this product! The quality is outstanding and customer service was amazing."
}
```

**Response:**
```json
{
  "overallSentiment": "positive",
  "sentimentScore": 0.92,
  "emotions": ["joy", "satisfaction", "excitement"],
  "confidence": 0.94
}
```

### 3. Text Summarization

**Endpoint:** `POST /api/ai/summarize`

**Request:**
```json
{
  "text": "Climate change is one of the most pressing issues of our time. Rising global temperatures are causing ice caps to melt, sea levels to rise, and weather patterns to become more extreme. Scientists agree that human activities, particularly the burning of fossil fuels, are the primary cause. Immediate action is needed to reduce greenhouse gas emissions and transition to renewable energy sources."
}
```

**Response:**
```json
{
  "summary": "Climate change is a critical global issue caused by human activities, requiring immediate action to reduce emissions and adopt renewable energy.",
  "keyPoints": [
    "Climate change is a pressing global issue",
    "Rising temperatures cause ice melt and extreme weather",
    "Human activities and fossil fuels are the main cause",
    "Urgent need for emission reduction and renewable energy"
  ],
  "wordCount": 28
}
```

### 4. Intent Detection

**Endpoint:** `POST /api/ai/intent`

**Request:**
```json
{
  "text": "Can you help me find the best Italian restaurant nearby?"
}
```

**Response:**
```json
{
  "primaryIntent": "find restaurant",
  "secondaryIntents": ["location search", "recommendation request"],
  "intentCategory": "question",
  "confidence": 0.91
}
```

## Testing with cURL

### Classification
```bash
curl -X POST http://localhost:8080/api/ai/classify \
  -H "Content-Type: application/json" \
  -d '{"text": "Breaking: New AI model achieves human-level performance"}'
```

### Sentiment Analysis
```bash
curl -X POST http://localhost:8080/api/ai/sentiment \
  -H "Content-Type: application/json" \
  -d '{"text": "This is the worst experience I have ever had!"}'
```

### Summarization
```bash
curl -X POST http://localhost:8080/api/ai/summarize \
  -H "Content-Type: application/json" \
  -d '{"text": "Your long text here..."}'
```

### Intent Detection
```bash
curl -X POST http://localhost:8080/api/ai/intent \
  -H "Content-Type: application/json" \
  -d '{"text": "I would like to book a flight to Paris next week"}'
```

## Troubleshooting

### Ollama Connection Issues
- Ensure Ollama is running: `ollama serve`
- Verify the model is pulled: `ollama list`
- Check the base URL in application.yml matches your Ollama setup

### Model Issues - Null or Empty Responses

If you're getting null values in API responses, the LLM model may be too small or not generating proper JSON.

**Recommended Models:**
- `llama3.2:3b` - Good balance of speed and quality (3 billion parameters)
- `llama3.1:8b` - Better quality for complex tasks (8 billion parameters)
- `mistral:7b` - Excellent for structured output (7 billion parameters)

**Small models like `smollm2:360m` may struggle with JSON generation.**

**To switch models:**
1. Pull a larger model:
   ```bash
   ollama pull llama3.2:3b
   ```

2. Update `application.yml`:
   ```yaml
   spring:
     ai:
       ollama:
         chat:
           options:
             model: llama3.2:3b
   ```

3. Restart the application

**If you must use a small model**, ensure your prompts are very explicit about the JSON format required.

### Build Issues
- Ensure Java 17 or higher is installed: `java -version`
- Clear Maven cache: `./mvnw clean`

## Dependencies

- Spring Boot 3.3.0
- Spring AI Ollama Starter 1.0.0-M5
- Spring Web
- Lombok (optional)

## License

This project is licensed under the MIT License.
