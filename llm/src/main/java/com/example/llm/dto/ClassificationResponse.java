package com.example.llm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Text classification result with labels and confidence")
public class ClassificationResponse {

    @Schema(description = "List of classification labels/tags", example = "[\"technology\", \"news\", \"AI\"]")
    @JsonProperty("labels")
    private List<String> labels;

    @Schema(description = "Primary category of the text", example = "technology")
    @JsonProperty("primaryCategory")
    private String primaryCategory;

    @Schema(description = "Confidence score (0.0 to 1.0)", example = "0.95")
    @JsonProperty("confidence")
    private Double confidence;

    public ClassificationResponse() {
    }

    public ClassificationResponse(List<String> labels, String primaryCategory, Double confidence) {
        this.labels = labels;
        this.primaryCategory = primaryCategory;
        this.confidence = confidence;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getPrimaryCategory() {
        return primaryCategory;
    }

    public void setPrimaryCategory(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
