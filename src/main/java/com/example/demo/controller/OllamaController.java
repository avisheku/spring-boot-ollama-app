package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api")
public class OllamaController {
    
    private static final Logger logger = LoggerFactory.getLogger(OllamaController.class);
    
    @Value("${ollama.base.url}")
    private String ollamaBaseUrl;
    
    private WebClient webClient;
    
    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
            .baseUrl(ollamaBaseUrl)
            .build();
    }
    
    @GetMapping("/health")
    public Mono<String> checkHealth() {
        return webClient.get()
            .uri("/")
            .retrieve()
            .bodyToMono(String.class)
            .map(response -> "Connected to Ollama")
            .onErrorReturn("Failed to connect to Ollama");
    }
    
    @PostMapping("/query")
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public ResponseEntity<String> queryOllama(@RequestBody String prompt) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(ollamaBaseUrl + "/api/generate");
            
            // Create proper JSON request body
            String jsonBody = String.format("""
                {
                    "model": "tinyllama",
                    "prompt": %s,
                    "stream": false
                }""", new ObjectMapper().writeValueAsString(prompt));
                
            request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
            request.setHeader("Accept", "application/json");
            
            String result = httpClient.execute(request, response -> {
                if (response.getCode() == 404) {
                    throw new RuntimeException("Model not found. Please ensure tinyllama model is pulled.");
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            });
            
            // Extract just the response text from Ollama's JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(result);
            String responseText = rootNode.path("response").asText();
            
            return ResponseEntity.ok(responseText);
        } catch (Exception e) {
            logger.error("Error querying Ollama: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
