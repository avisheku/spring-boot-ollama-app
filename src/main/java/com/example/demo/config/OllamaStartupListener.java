package com.example.demo.config;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class OllamaStartupListener implements ApplicationListener<ApplicationStartedEvent> {

    @Value("${ollama.base.url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println("Waiting for Ollama to be ready...");
        while (!isOllamaReady()) {
            try {
                Thread.sleep(2000);
                System.out.println("Retrying connection to Ollama...");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for Ollama", e);
            }
        }
        System.out.println("Ollama is ready!");
    }

    private boolean isOllamaReady() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(ollamaBaseUrl);
            return httpClient.execute(request, response -> response.getCode() == 200);
        } catch (Exception e) {
            return false;
        }
    }
} 