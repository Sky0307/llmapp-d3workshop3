package com.example.llm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local Development Server");

        Contact contact = new Contact();
        contact.setName("AI API Support");
        contact.setEmail("support@example.com");

        Info info = new Info()
                .title("Spring AI with Ollama - Text Analysis API")
                .version("1.0.0")
                .description("RESTful APIs for AI-powered text analysis including classification, " +
                        "sentiment analysis, summarization, and intent detection using Ollama with Llama models.")
                .contact(contact);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
