package com.example.llm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.example.llm.dto.*;

@Service
public class AIService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public AIService(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public ClassificationResponse classifyText(String text) {
        String promptText =
            "Analyze the following text and classify it with appropriate labels and tags. " +
            "Respond with ONLY valid JSON, no additional text or explanation.\n\n" +
            "Text: " + text + "\n\n" +
            "Return JSON in this exact format:\n" +
            "{\"labels\": [\"label1\", \"label2\"], \"primaryCategory\": \"category\", \"confidence\": 0.9}";

        String response = chatClient.prompt()
            .user(promptText)
            .call()
            .content();

        return parseJson(response, ClassificationResponse.class);
    }

    public SentimentResponse analyzeSentiment(String text) {
        String promptText =
            "Analyze the sentiment of the following text. " +
            "Respond with ONLY valid JSON, no additional text or explanation.\n\n" +
            "Text: " + text + "\n\n" +
            "Return JSON in this exact format:\n" +
            "{\"overallSentiment\": \"positive\", \"sentimentScore\": 0.8, \"emotions\": [\"joy\", \"excitement\"], \"confidence\": 0.9}";

        String response = chatClient.prompt()
            .user(promptText)
            .call()
            .content();

        return parseJson(response, SentimentResponse.class);
    }

    public SummaryResponse summarizeText(String text) {
        String promptText =
            "Summarize the following text concisely. " +
            "Respond with ONLY valid JSON, no additional text or explanation.\n\n" +
            "Text: " + text + "\n\n" +
            "Return JSON in this exact format:\n" +
            "{\"summary\": \"your summary here\", \"keyPoints\": [\"point1\", \"point2\", \"point3\"], \"wordCount\": 25}";

        String response = chatClient.prompt()
            .user(promptText)
            .call()
            .content();

        return parseJson(response, SummaryResponse.class);
    }

    public IntentResponse detectIntent(String text) {
        String promptText =
            "Detect the intent behind the following text. " +
            "Respond with ONLY valid JSON, no additional text or explanation.\n\n" +
            "Text: " + text + "\n\n" +
            "Return JSON in this exact format:\n" +
            "{\"primaryIntent\": \"main_intent\", \"secondaryIntents\": [\"intent1\", \"intent2\"], \"intentCategory\": \"question\", \"confidence\": 0.9}";

        String response = chatClient.prompt()
            .user(promptText)
            .call()
            .content();

        return parseJson(response, IntentResponse.class);
    }

    private <T> T parseJson(String json, Class<T> clazz) {
        try {
            // Strip markdown code blocks if present
            String cleaned = json.trim();
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7);
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3);
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            return objectMapper.readValue(cleaned.trim(), clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response as JSON: " + json, e);
        }
    }
}
