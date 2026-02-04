package com.example.llm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.llm.dto.*;
import com.example.llm.service.AIService;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Text Analysis", description = "APIs for AI-powered text analysis using Ollama")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @Operation(
        summary = "Classify Text",
        description = "Analyzes text and returns classification labels, tags, and primary category"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Text classified successfully",
            content = @Content(schema = @Schema(implementation = ClassificationResponse.class))
        )
    })
    @PostMapping("/classify")
    public ResponseEntity<ClassificationResponse> classifyText(@RequestBody TextRequest request) {
        ClassificationResponse response = aiService.classifyText(request.getText());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Analyze Sentiment",
        description = "Analyzes text sentiment (positive, negative, neutral) and detects specific emotions"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Sentiment analyzed successfully",
            content = @Content(schema = @Schema(implementation = SentimentResponse.class))
        )
    })
    @PostMapping("/sentiment")
    public ResponseEntity<SentimentResponse> analyzeSentiment(@RequestBody TextRequest request) {
        SentimentResponse response = aiService.analyzeSentiment(request.getText());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Summarize Text",
        description = "Generates a concise summary with key points from the provided text"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Text summarized successfully",
            content = @Content(schema = @Schema(implementation = SummaryResponse.class))
        )
    })
    @PostMapping("/summarize")
    public ResponseEntity<SummaryResponse> summarizeText(@RequestBody TextRequest request) {
        SummaryResponse response = aiService.summarizeText(request.getText());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Detect Intent",
        description = "Identifies the intent and purpose behind the text (question, request, statement, command)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Intent detected successfully",
            content = @Content(schema = @Schema(implementation = IntentResponse.class))
        )
    })
    @PostMapping("/intent")
    public ResponseEntity<IntentResponse> detectIntent(@RequestBody TextRequest request) {
        IntentResponse response = aiService.detectIntent(request.getText());
        return ResponseEntity.ok(response);
    }
}
