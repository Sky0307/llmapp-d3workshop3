package com.example.llm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Intent detection result with primary and secondary intents")
public class IntentResponse {

    @Schema(description = "Primary intent detected", example = "find_restaurant")
    @JsonProperty("primaryIntent")
    private String primaryIntent;

    @Schema(description = "Secondary intents detected", example = "[\"location_search\", \"recommendation_request\"]")
    @JsonProperty("secondaryIntents")
    private List<String> secondaryIntents;

    @Schema(description = "Category of the intent", example = "question", allowableValues = {"question", "request", "statement", "command"})
    @JsonProperty("intentCategory")
    private String intentCategory;

    @Schema(description = "Confidence score (0.0 to 1.0)", example = "0.88")
    @JsonProperty("confidence")
    private Double confidence;

    public IntentResponse() {
    }

    public IntentResponse(String primaryIntent, List<String> secondaryIntents, String intentCategory, Double confidence) {
        this.primaryIntent = primaryIntent;
        this.secondaryIntents = secondaryIntents;
        this.intentCategory = intentCategory;
        this.confidence = confidence;
    }

    public String getPrimaryIntent() {
        return primaryIntent;
    }

    public void setPrimaryIntent(String primaryIntent) {
        this.primaryIntent = primaryIntent;
    }

    public List<String> getSecondaryIntents() {
        return secondaryIntents;
    }

    public void setSecondaryIntents(List<String> secondaryIntents) {
        this.secondaryIntents = secondaryIntents;
    }

    public String getIntentCategory() {
        return intentCategory;
    }

    public void setIntentCategory(String intentCategory) {
        this.intentCategory = intentCategory;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
