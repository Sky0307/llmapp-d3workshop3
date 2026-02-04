package com.example.llm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Text summarization result with key points")
public class SummaryResponse {

    @Schema(description = "Concise summary of the text", example = "This article discusses the impact of AI on healthcare...")
    @JsonProperty("summary")
    private String summary;

    @Schema(description = "Key points extracted from the text", example = "[\"AI improves diagnosis\", \"Reduces costs\", \"Enhances patient care\"]")
    @JsonProperty("keyPoints")
    private List<String> keyPoints;

    @Schema(description = "Word count of the summary", example = "50")
    @JsonProperty("wordCount")
    private Integer wordCount;

    public SummaryResponse() {
    }

    public SummaryResponse(String summary, List<String> keyPoints, Integer wordCount) {
        this.summary = summary;
        this.keyPoints = keyPoints;
        this.wordCount = wordCount;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getKeyPoints() {
        return keyPoints;
    }

    public void setKeyPoints(List<String> keyPoints) {
        this.keyPoints = keyPoints;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }
}
