package com.example.llm.service;

import com.example.llm.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    private ObjectMapper objectMapper;
    private AIService aiService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        when(chatClientBuilder.build()).thenReturn(chatClient);
        aiService = new AIService(chatClientBuilder, objectMapper);
    }

    private void setupChatClientMock(String response) {
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(response);
    }

    @Nested
    @DisplayName("classifyText tests")
    class ClassifyTextTests {

        @Test
        @DisplayName("Should parse valid JSON response")
        void classifyText_WithValidJsonResponse_ReturnsClassificationResponse() {
            // Arrange
            String jsonResponse = "{\"labels\": [\"technology\", \"AI\"], \"primaryCategory\": \"technology\", \"confidence\": 0.95}";
            setupChatClientMock(jsonResponse);

            // Act
            ClassificationResponse result = aiService.classifyText("AI is transforming healthcare");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getLabels()).containsExactly("technology", "AI");
            assertThat(result.getPrimaryCategory()).isEqualTo("technology");
            assertThat(result.getConfidence()).isEqualTo(0.95);
        }

        @Test
        @DisplayName("Should handle JSON wrapped in markdown code blocks")
        void classifyText_WithMarkdownWrappedJson_ParsesSuccessfully() {
            // Arrange
            String jsonResponse = "```json\n{\"labels\": [\"news\"], \"primaryCategory\": \"news\", \"confidence\": 0.8}\n```";
            setupChatClientMock(jsonResponse);

            // Act
            ClassificationResponse result = aiService.classifyText("Breaking news article");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getLabels()).containsExactly("news");
            assertThat(result.getPrimaryCategory()).isEqualTo("news");
        }

        @Test
        @DisplayName("Should handle JSON wrapped in plain code blocks")
        void classifyText_WithPlainCodeBlockWrappedJson_ParsesSuccessfully() {
            // Arrange
            String jsonResponse = "```\n{\"labels\": [\"sports\"], \"primaryCategory\": \"sports\", \"confidence\": 0.9}\n```";
            setupChatClientMock(jsonResponse);

            // Act
            ClassificationResponse result = aiService.classifyText("Football game results");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getPrimaryCategory()).isEqualTo("sports");
        }

        @Test
        @DisplayName("Should throw exception for invalid JSON")
        void classifyText_WithInvalidJson_ThrowsRuntimeException() {
            // Arrange
            String invalidJson = "This is not valid JSON";
            setupChatClientMock(invalidJson);

            // Act & Assert
            assertThatThrownBy(() -> aiService.classifyText("some text"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to parse AI response as JSON");
        }

        @Test
        @DisplayName("Should handle empty labels array")
        void classifyText_WithEmptyLabels_ReturnsEmptyList() {
            // Arrange
            String jsonResponse = "{\"labels\": [], \"primaryCategory\": \"unknown\", \"confidence\": 0.5}";
            setupChatClientMock(jsonResponse);

            // Act
            ClassificationResponse result = aiService.classifyText("ambiguous text");

            // Assert
            assertThat(result.getLabels()).isEmpty();
        }
    }

    @Nested
    @DisplayName("analyzeSentiment tests")
    class AnalyzeSentimentTests {

        @Test
        @DisplayName("Should parse positive sentiment response")
        void analyzeSentiment_WithPositiveSentiment_ReturnsSentimentResponse() {
            // Arrange
            String jsonResponse = "{\"overallSentiment\": \"positive\", \"sentimentScore\": 0.85, \"emotions\": [\"joy\", \"excitement\"], \"confidence\": 0.92}";
            setupChatClientMock(jsonResponse);

            // Act
            SentimentResponse result = aiService.analyzeSentiment("I love this!");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getOverallSentiment()).isEqualTo("positive");
            assertThat(result.getSentimentScore()).isEqualTo(0.85);
            assertThat(result.getEmotions()).containsExactly("joy", "excitement");
            assertThat(result.getConfidence()).isEqualTo(0.92);
        }

        @Test
        @DisplayName("Should parse negative sentiment response")
        void analyzeSentiment_WithNegativeSentiment_ReturnsSentimentResponse() {
            // Arrange
            String jsonResponse = "{\"overallSentiment\": \"negative\", \"sentimentScore\": -0.75, \"emotions\": [\"anger\", \"disappointment\"], \"confidence\": 0.88}";
            setupChatClientMock(jsonResponse);

            // Act
            SentimentResponse result = aiService.analyzeSentiment("This is terrible!");

            // Assert
            assertThat(result.getOverallSentiment()).isEqualTo("negative");
            assertThat(result.getSentimentScore()).isEqualTo(-0.75);
            assertThat(result.getEmotions()).contains("anger");
        }

        @Test
        @DisplayName("Should parse neutral sentiment with empty emotions")
        void analyzeSentiment_WithNeutralSentiment_ReturnsEmptyEmotions() {
            // Arrange
            String jsonResponse = "{\"overallSentiment\": \"neutral\", \"sentimentScore\": 0.0, \"emotions\": [], \"confidence\": 0.95}";
            setupChatClientMock(jsonResponse);

            // Act
            SentimentResponse result = aiService.analyzeSentiment("The meeting is at 3 PM.");

            // Assert
            assertThat(result.getOverallSentiment()).isEqualTo("neutral");
            assertThat(result.getSentimentScore()).isEqualTo(0.0);
            assertThat(result.getEmotions()).isEmpty();
        }

        @Test
        @DisplayName("Should handle markdown wrapped response")
        void analyzeSentiment_WithMarkdownWrapped_ParsesSuccessfully() {
            // Arrange
            String jsonResponse = "```json\n{\"overallSentiment\": \"positive\", \"sentimentScore\": 0.9, \"emotions\": [\"happiness\"], \"confidence\": 0.95}\n```";
            setupChatClientMock(jsonResponse);

            // Act
            SentimentResponse result = aiService.analyzeSentiment("Great news!");

            // Assert
            assertThat(result.getOverallSentiment()).isEqualTo("positive");
        }
    }

    @Nested
    @DisplayName("summarizeText tests")
    class SummarizeTextTests {

        @Test
        @DisplayName("Should parse valid summary response")
        void summarizeText_WithValidResponse_ReturnsSummaryResponse() {
            // Arrange
            String jsonResponse = "{\"summary\": \"AI transforms healthcare through improved diagnostics.\", \"keyPoints\": [\"AI improves diagnosis\", \"Reduces costs\", \"Enhances care\"], \"wordCount\": 6}";
            setupChatClientMock(jsonResponse);

            // Act
            SummaryResponse result = aiService.summarizeText("Long article about AI in healthcare...");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getSummary()).isEqualTo("AI transforms healthcare through improved diagnostics.");
            assertThat(result.getKeyPoints()).hasSize(3);
            assertThat(result.getWordCount()).isEqualTo(6);
        }

        @Test
        @DisplayName("Should handle single key point")
        void summarizeText_WithSingleKeyPoint_ReturnsSingleItemList() {
            // Arrange
            String jsonResponse = "{\"summary\": \"Brief summary.\", \"keyPoints\": [\"Main point\"], \"wordCount\": 2}";
            setupChatClientMock(jsonResponse);

            // Act
            SummaryResponse result = aiService.summarizeText("Short text");

            // Assert
            assertThat(result.getKeyPoints()).hasSize(1);
            assertThat(result.getKeyPoints().get(0)).isEqualTo("Main point");
        }

        @Test
        @DisplayName("Should handle empty key points")
        void summarizeText_WithNoKeyPoints_ReturnsEmptyList() {
            // Arrange
            String jsonResponse = "{\"summary\": \"Too short to summarize.\", \"keyPoints\": [], \"wordCount\": 4}";
            setupChatClientMock(jsonResponse);

            // Act
            SummaryResponse result = aiService.summarizeText("Hi");

            // Assert
            assertThat(result.getKeyPoints()).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception for malformed JSON")
        void summarizeText_WithMalformedJson_ThrowsRuntimeException() {
            // Arrange
            String malformedJson = "{summary: missing quotes}";
            setupChatClientMock(malformedJson);

            // Act & Assert
            assertThatThrownBy(() -> aiService.summarizeText("some text"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to parse AI response as JSON");
        }
    }

    @Nested
    @DisplayName("detectIntent tests")
    class DetectIntentTests {

        @Test
        @DisplayName("Should parse question intent response")
        void detectIntent_WithQuestionIntent_ReturnsIntentResponse() {
            // Arrange
            String jsonResponse = "{\"primaryIntent\": \"find_restaurant\", \"secondaryIntents\": [\"location_search\", \"recommendation\"], \"intentCategory\": \"question\", \"confidence\": 0.88}";
            setupChatClientMock(jsonResponse);

            // Act
            IntentResponse result = aiService.detectIntent("Where is the nearest restaurant?");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getPrimaryIntent()).isEqualTo("find_restaurant");
            assertThat(result.getSecondaryIntents()).containsExactly("location_search", "recommendation");
            assertThat(result.getIntentCategory()).isEqualTo("question");
            assertThat(result.getConfidence()).isEqualTo(0.88);
        }

        @Test
        @DisplayName("Should parse command intent response")
        void detectIntent_WithCommandIntent_ReturnsCommandCategory() {
            // Arrange
            String jsonResponse = "{\"primaryIntent\": \"turn_off_lights\", \"secondaryIntents\": [\"smart_home\"], \"intentCategory\": \"command\", \"confidence\": 0.95}";
            setupChatClientMock(jsonResponse);

            // Act
            IntentResponse result = aiService.detectIntent("Turn off the lights");

            // Assert
            assertThat(result.getPrimaryIntent()).isEqualTo("turn_off_lights");
            assertThat(result.getIntentCategory()).isEqualTo("command");
        }

        @Test
        @DisplayName("Should parse request intent response")
        void detectIntent_WithRequestIntent_ReturnsRequestCategory() {
            // Arrange
            String jsonResponse = "{\"primaryIntent\": \"send_document\", \"secondaryIntents\": [], \"intentCategory\": \"request\", \"confidence\": 0.90}";
            setupChatClientMock(jsonResponse);

            // Act
            IntentResponse result = aiService.detectIntent("Please send me the report");

            // Assert
            assertThat(result.getIntentCategory()).isEqualTo("request");
            assertThat(result.getSecondaryIntents()).isEmpty();
        }

        @Test
        @DisplayName("Should parse statement intent response")
        void detectIntent_WithStatementIntent_ReturnsStatementCategory() {
            // Arrange
            String jsonResponse = "{\"primaryIntent\": \"share_observation\", \"secondaryIntents\": [\"weather\"], \"intentCategory\": \"statement\", \"confidence\": 0.85}";
            setupChatClientMock(jsonResponse);

            // Act
            IntentResponse result = aiService.detectIntent("The weather is nice today");

            // Assert
            assertThat(result.getIntentCategory()).isEqualTo("statement");
        }

        @Test
        @DisplayName("Should handle markdown code block wrapper")
        void detectIntent_WithMarkdownWrapper_ParsesSuccessfully() {
            // Arrange
            String jsonResponse = "```json\n{\"primaryIntent\": \"greeting\", \"secondaryIntents\": [], \"intentCategory\": \"statement\", \"confidence\": 0.99}\n```";
            setupChatClientMock(jsonResponse);

            // Act
            IntentResponse result = aiService.detectIntent("Hello!");

            // Assert
            assertThat(result.getPrimaryIntent()).isEqualTo("greeting");
            assertThat(result.getConfidence()).isEqualTo(0.99);
        }
    }

    @Nested
    @DisplayName("JSON parsing edge cases")
    class JsonParsingEdgeCases {

        @Test
        @DisplayName("Should handle extra whitespace in response")
        void parseJson_WithExtraWhitespace_ParsesSuccessfully() {
            // Arrange
            String jsonResponse = "  \n\n  {\"labels\": [\"test\"], \"primaryCategory\": \"test\", \"confidence\": 0.9}  \n\n  ";
            setupChatClientMock(jsonResponse);

            // Act
            ClassificationResponse result = aiService.classifyText("test");

            // Assert
            assertThat(result.getPrimaryCategory()).isEqualTo("test");
        }

        @Test
        @DisplayName("Should throw exception for JSON with unknown fields (default ObjectMapper behavior)")
        void parseJson_WithExtraFields_ThrowsException() {
            // Arrange - default ObjectMapper does not ignore unknown properties
            String jsonResponse = "{\"labels\": [\"test\"], \"primaryCategory\": \"test\", \"confidence\": 0.9, \"extraField\": \"ignored\"}";
            setupChatClientMock(jsonResponse);

            // Act & Assert - should throw because ObjectMapper is not configured to ignore unknown
            assertThatThrownBy(() -> aiService.classifyText("test"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to parse AI response as JSON");
        }

        @Test
        @DisplayName("Should handle nested markdown blocks")
        void parseJson_WithOnlyClosingCodeBlock_HandlesGracefully() {
            // Arrange
            String jsonResponse = "{\"overallSentiment\": \"neutral\", \"sentimentScore\": 0.0, \"emotions\": [], \"confidence\": 0.5}```";
            setupChatClientMock(jsonResponse);

            // Act
            SentimentResponse result = aiService.analyzeSentiment("test");

            // Assert
            assertThat(result.getOverallSentiment()).isEqualTo("neutral");
        }

        @Test
        @DisplayName("Should verify ChatClient is called with correct prompt structure")
        void chatClient_CalledWithCorrectPrompt_ContainsExpectedElements() {
            // Arrange
            String jsonResponse = "{\"labels\": [\"test\"], \"primaryCategory\": \"test\", \"confidence\": 0.9}";
            setupChatClientMock(jsonResponse);

            // Act
            aiService.classifyText("Test input text");

            // Assert
            verify(chatClient).prompt();
            verify(requestSpec).user(argThat((String prompt) ->
                prompt.contains("Test input text") &&
                prompt.contains("Respond with ONLY valid JSON") &&
                prompt.contains("labels") &&
                prompt.contains("primaryCategory") &&
                prompt.contains("confidence")
            ));
            verify(requestSpec).call();
            verify(callResponseSpec).content();
        }
    }
}
