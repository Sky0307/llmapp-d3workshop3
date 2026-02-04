package com.example.llm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body containing text to analyze")
public class TextRequest {

    @Schema(description = "Text to be analyzed", example = "I love this product! The quality is outstanding.", required = true)
    @JsonProperty("text")
    private String text;

    public TextRequest() {
    }

    public TextRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
