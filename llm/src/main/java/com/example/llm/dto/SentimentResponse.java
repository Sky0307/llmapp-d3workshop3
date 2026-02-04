package com.example.llm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Sentiment analysis result with emotions and confidence")
public class SentimentResponse {

    @Schema(description = "Overall sentiment classification", example = "positive", allowableValues = {"positive", "negative", "neutral"})
    @JsonProperty("overallSentiment")
    private String overallSentiment;

    @Schema(description = "Sentiment score from -1 (negative) to 1 (positive)", example = "0.85")
    @JsonProperty("sentimentScore")
    private Double sentimentScore;

    @Schema(description = "Detected emotions in the text", example = "[\"joy\", \"excitement\"]")
    @JsonProperty("emotions")
    private List<String> emotions;

    @Schema(description = "Confidence score (0.0 to 1.0)", example = "0.92")
    @JsonProperty("confidence")
    private Double confidence;

    public SentimentResponse() {
    }

    public SentimentResponse(String overallSentiment, Double sentimentScore, List<String> emotions, Double confidence) {
        this.overallSentiment = overallSentiment;
        this.sentimentScore = sentimentScore;
        this.emotions = emotions;
        this.confidence = confidence;
    }

    public String getOverallSentiment() {
        return overallSentiment;
    }

    public void setOverallSentiment(String overallSentiment) {
        this.overallSentiment = overallSentiment;
    }

    public Double getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(Double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public List<String> getEmotions() {
        return emotions;
    }

    public void setEmotions(List<String> emotions) {
        this.emotions = emotions;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
