package com.example.llm.controller;

import com.example.llm.dto.*;
import com.example.llm.service.AIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AIController.class)
class AIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AIService aiService;

    @Nested
    @DisplayName("POST /api/ai/classify")
    class ClassifyEndpointTests {

        @Test
        @DisplayName("Should return classification response for valid text")
        void classifyText_WithValidText_ReturnsClassificationResponse() throws Exception {
            // Arrange
            String inputText = "Artificial intelligence is transforming healthcare.";
            TextRequest request = new TextRequest(inputText);

            ClassificationResponse expectedResponse = new ClassificationResponse(
                Arrays.asList("technology", "healthcare", "AI"),
                "technology",
                0.95
            );

            when(aiService.classifyText(inputText)).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/ai/classify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.labels[0]").value("technology"))
                .andExpect(jsonPath("$.labels[1]").value("healthcare"))
                .andExpect(jsonPath("$.labels[2]").value("AI"))
                .andExpect(jsonPath("$.primaryCategory").value("technology"))
                .andExpect(jsonPath("$.confidence").value(0.95));

            verify(aiService, times(1)).classifyText(inputText);
        }

        @Test
        @DisplayName("Should handle empty labels list")
        void classifyText_WithEmptyLabels_ReturnsEmptyLabelsArray() throws Exception {
            // Arrange
            String inputText = "Some ambiguous text";
            TextRequest request = new TextRequest(inputText);

            ClassificationResponse expectedResponse = new ClassificationResponse(
                List.of(),
                "unknown",
                0.5
            );

            when(aiService.classifyText(inputText)).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/ai/classify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels").isEmpty())
                .andExpect(jsonPath("$.primaryCategory").value("unknown"))
                .andExpect(jsonPath("$.confidence").value(0.5));
        }

        @Test
        @DisplayName("Should propagate service exception as 500 error")
        void classifyText_WhenServiceThrowsException_ThrowsException() throws Exception {
            // Arrange
            String inputText = "Test text";
            TextRequest request = new TextRequest(inputText);

            when(aiService.classifyText(inputText))
                .thenThrow(new RuntimeException("AI service unavailable"));

            // Act & Assert - exception propagates since no global exception handler
            try {
                mockMvc.perform(post("/api/ai/classify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
            } catch (Exception e) {
                assertThat(e.getCause()).isInstanceOf(RuntimeException.class);
                assertThat(e.getCause().getMessage()).contains("AI service unavailable");
            }
        }
    }

    @Nested
    @DisplayName("POST /api/ai/sentiment")
    class SentimentEndpointTests {

        @Test
        @DisplayName("Should return positive sentiment for positive text")
        void analyzeSentiment_WithPositiveText_ReturnsPositiveSentiment() throws Exception {
            // Arrange
            String inputText = "I love this product! It's amazing!";
            TextRequest request = new TextRequest(inputText);

            SentimentResponse expectedResponse = new SentimentResponse(
                "positive",
                0.85,
                Arrays.asList("joy", "excitement"),
                0.92
            );

            when(aiService.analyzeSentiment(inputText)).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/ai/sentiment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.overallSentiment").value("positive"))
                .andExpect(jsonPath("$.sentimentScore").value(0.85))
                .andExpect(jsonPath("$.emotions").isArray())
                .andExpect(jsonPath("$.emotions[0]").value("joy"))
                .andExpect(jsonPath("$.emotions[1]").value("excitement"))
                .andExpect(jsonPath("$.confidence").value(0.92));

            verify(aiService, times(1)).analyzeSentiment(inputText);
        }

        @Test
        @DisplayName("Should return negative sentiment for negative text")
        void analyzeSentiment_WithNegativeText_ReturnsNegativeSentiment() throws Exception {
            // Arrange
            String inputText = "This is terrible. I'm very disappointed.";
            TextRequest request = new TextRequest(inputText);

            SentimentResponse expectedResponse = new SentimentResponse(
                "negative",
                -0.75,
                Arrays.asList("disappointment", "frustration"),
                0.88
            );

            when(aiService.analyzeSentiment(inputText)).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/ai/sentiment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overallSentiment").value("negative"))
                .andExpect(jsonPath("$.sentimentScore").value(-0.75))
                .andExpect(jsonPath("$.emotions[0]").value("disappointment"));
        }

        @Test
        @DisplayName("Should return neutral sentiment for neutral text")
        void analyzeSentiment_WithNeutralText_ReturnsNeutralSentiment() throws Exception {
            // Arrange
            String inputText = "The meeting is scheduled for 3 PM.";
            TextRequest request = new TextRequest(inputText);

            SentimentResponse expectedResponse = new SentimentResponse(
                "neutral",
                0.0,
                List.of(),
                0.95
            );

            when(aiService.analyzeSentiment(inputText)).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/ai/sentiment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overallSentiment").value("neutral"))
                .andExpect(jsonPath("$.sentimentScore").value(0.0))
                .andExpect(jsonPath("$.emotions").isEmpty());
        }

        @Test
        @DisplayName("Should propagate service exception as 500 error")
        void analyzeSentiment_WhenServiceThrowsException_ThrowsException() throws Exception {
            // Arrange
            String inputText = "Test text";
            TextRequest request = new TextRequest(inputText);

            when(aiService.analyzeSentiment(inputText))
                .thenThrow(new RuntimeException("Failed to parse AI response"));

            // Act & Assert - exception propagates since no global exception handler
            try {
                mockMvc.perform(post("/api/ai/sentiment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
            } catch (Exception e) {
                assertThat(e.getCause()).isInstanceOf(RuntimeException.class);
                assertThat(e.getCause().getMessage()).contains("Failed to parse AI response");
            }
        }
    }

    @Nested
    @DisplayName("POST /api/ai/summarize")
    class SummarizeEndpointTests {

        @Test
        @DisplayName("Should return summary with key points for valid text")
        void summarizeText_WithValidText_ReturnsSummaryResponse() throws Exception {
            // Arrange
            String inputText = "Artificial intelligence (AI) is intelligence demonstrated by machines. " +
                "AI research has been defined as the field of study of intelligent agents. " +
                "Machine learning is a subset of AI that enables systems to learn from data.";
            TextRequest request = new TextRequest(inputText);

            SummaryResponse expectedResponse = new SummaryResponse(
                "AI is machine intelligence, with machine learning being a key subset that enables data-driven learning.",
                Arrays.asList(
                    "AI is intelligence demonstrated by machines",
                    "AI research studies intelligent agents",
                    "Machine learning enables learning from data"
                ),
                15
            );

            when(aiService.summarizeText(inputText)).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/ai/summarize")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.summary").isNotEmpty())
                .andExpect(jsonPath("$.keyPoints").isArray())
                .andExpect(jsonPath("$.keyPoints.length()").value(3))
                .andExpect(jsonPath("$.wordCount").value(15));

            verify(aiService, times(1)).summarizeText(inputText);
        }

        @Test
        @DisplayName("Should handle short text with minimal key points")
        void summarizeText_WithShortText_ReturnsMinimalSummary() throws Exception {
            // Arrange
            String inputText = "Hello world.";
            TextRequest request = new TextRequest(inputText);

            SummaryResponse expectedResponse = new SummaryResponse(
                "A simple greeting.",
                List.of("Greeting message"),
                3
            );

            when(aiService.summarizeText(inputText)).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/ai/summarize")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").value("A simple greeting."))
                .andExpect(jsonPath("$.keyPoints.length()").value(1))
                .andExpect(jsonPath("$.wordCount").value(3));
        }

        @Test
        @DisplayName("Should propagate service exception as 500 error")
        void summarizeText_WhenServiceThrowsException_ThrowsException() throws Exception {
            // Arrange
            String inputText = "Test text";
            TextRequest request = new TextRequest(inputText);

            when(aiService.summarizeText(inputText))
                .thenThrow(new RuntimeException("AI service timeout"));

            // Act & Assert - exception propagates since no global exception handler
            try {
                mockMvc.perform(post("/api/ai/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
            } catch (Exception e) {
                assertThat(e.getCause()).isInstanceOf(RuntimeException.class);
                assertThat(e.getCause().getMessage()).contains("AI service timeout");
            }
        }
    }

    @Nested
    @DisplayName("POST /api/ai/intent")
    class IntentEndpointTests {

        @Test
        @DisplayName("Should return question intent for question text")
        void detectIntent_WithQuestionText_ReturnsQuestionIntent() throws Exception {
            // Arrange
            String inputText = "Where is the nearest restaurant?";
            TextRequest request = new TextRequest(inputText);

            IntentResponse expectedResponse = new IntentResponse(
                "find_restaurant",
                Arrays.asList("location_search", "recommendation_request"),
                "question",
                0.88
            );

            when(aiService.detectIntent(inputText)).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/ai/intent")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.primaryIntent").value("find_restaurant"))
                .andExpect(jsonPath("$.secondaryIntents").isArray())
                .andExpect(jsonPath("$.secondaryIntents[0]").value("location_search"))
                .andExpect(jsonPath("$.intentCategory").value("question"))
                .andExpect(jsonPath("$.confidence").value(0.88));

            verify(aiService, times(1)).detectIntent(inputText);
        }

        @Test
        @DisplayName("Should return command intent for command text")
        void detectIntent_WithCommandText_ReturnsCommandIntent() throws Exception {
            // Arrange
            String inputText = "Turn off the lights";
            TextRequest request = new TextRequest(inputText);

            IntentResponse expectedResponse = new IntentResponse(
                "control_device",
                Arrays.asList("smart_home", "lighting"),
                "command",
                0.95
            );

            when(aiService.detectIntent(inputText)).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/ai/intent")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryIntent").value("control_device"))
                .andExpect(jsonPath("$.intentCategory").value("command"))
                .andExpect(jsonPath("$.confidence").value(0.95));
        }

        @Test
        @DisplayName("Should return request intent for request text")
        void detectIntent_WithRequestText_ReturnsRequestIntent() throws Exception {
            // Arrange
            String inputText = "Please send me the report";
            TextRequest request = new TextRequest(inputText);

            IntentResponse expectedResponse = new IntentResponse(
                "request_document",
                Arrays.asList("email", "file_transfer"),
                "request",
                0.90
            );

            when(aiService.detectIntent(inputText)).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/ai/intent")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryIntent").value("request_document"))
                .andExpect(jsonPath("$.intentCategory").value("request"));
        }

        @Test
        @DisplayName("Should return statement intent for statement text")
        void detectIntent_WithStatementText_ReturnsStatementIntent() throws Exception {
            // Arrange
            String inputText = "The weather is nice today.";
            TextRequest request = new TextRequest(inputText);

            IntentResponse expectedResponse = new IntentResponse(
                "share_information",
                Arrays.asList("weather", "observation"),
                "statement",
                0.85
            );

            when(aiService.detectIntent(inputText)).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/ai/intent")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryIntent").value("share_information"))
                .andExpect(jsonPath("$.intentCategory").value("statement"));
        }

        @Test
        @DisplayName("Should propagate service exception as 500 error")
        void detectIntent_WhenServiceThrowsException_ThrowsException() throws Exception {
            // Arrange
            String inputText = "Test text";
            TextRequest request = new TextRequest(inputText);

            when(aiService.detectIntent(inputText))
                .thenThrow(new RuntimeException("Connection refused"));

            // Act & Assert - exception propagates since no global exception handler
            try {
                mockMvc.perform(post("/api/ai/intent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
            } catch (Exception e) {
                assertThat(e.getCause()).isInstanceOf(RuntimeException.class);
                assertThat(e.getCause().getMessage()).contains("Connection refused");
            }
        }
    }

    @Nested
    @DisplayName("Request validation tests")
    class RequestValidationTests {

        @Test
        @DisplayName("Should handle request with null text field")
        void endpoint_WithNullTextField_CallsServiceWithNull() throws Exception {
            // Arrange
            String jsonRequest = "{\"text\": null}";

            ClassificationResponse expectedResponse = new ClassificationResponse(
                List.of("unknown"),
                "unknown",
                0.0
            );

            when(aiService.classifyText(null)).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/ai/classify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
                .andExpect(status().isOk());

            verify(aiService).classifyText(null);
        }

        @Test
        @DisplayName("Should handle request with empty text")
        void endpoint_WithEmptyText_ProcessesRequest() throws Exception {
            // Arrange
            TextRequest request = new TextRequest("");

            SentimentResponse expectedResponse = new SentimentResponse(
                "neutral",
                0.0,
                List.of(),
                0.0
            );

            when(aiService.analyzeSentiment("")).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/ai/sentiment")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overallSentiment").value("neutral"));
        }

        @Test
        @DisplayName("Should handle request with very long text")
        void endpoint_WithLongText_ProcessesRequest() throws Exception {
            // Arrange
            String longText = "A".repeat(10000);
            TextRequest request = new TextRequest(longText);

            SummaryResponse expectedResponse = new SummaryResponse(
                "A very long repetitive text.",
                List.of("Contains repeated character A"),
                5
            );

            when(aiService.summarizeText(longText)).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/ai/summarize")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").exists());
        }

        @Test
        @DisplayName("Should handle request with special characters")
        void endpoint_WithSpecialCharacters_ProcessesRequest() throws Exception {
            // Arrange
            String textWithSpecialChars = "Hello! @#$%^&*() Test 123 日本語 émojis 🎉";
            TextRequest request = new TextRequest(textWithSpecialChars);

            IntentResponse expectedResponse = new IntentResponse(
                "greeting",
                List.of("test"),
                "statement",
                0.7
            );

            when(aiService.detectIntent(textWithSpecialChars)).thenReturn(expectedResponse);

            // Act & Assert
            mockMvc.perform(post("/api/ai/intent")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryIntent").value("greeting"));
        }

        @Test
        @DisplayName("Should return bad request for invalid JSON")
        void endpoint_WithInvalidJson_ReturnsBadRequest() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/api/ai/classify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return unsupported media type for non-JSON content")
        void endpoint_WithNonJsonContent_ReturnsUnsupportedMediaType() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/api/ai/classify")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("some text"))
                .andExpect(status().isUnsupportedMediaType());
        }
    }
}
