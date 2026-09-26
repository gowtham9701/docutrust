package com.docutrust.api.analysis;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class OpenRouterConfiguration {
    @Bean
    OpenRouterLlmProvider.OpenRouterProperties openRouterProperties() {
        return new OpenRouterLlmProvider.OpenRouterProperties(
            System.getenv("OPENROUTER_API_KEY"),
            env("OPENROUTER_BASE_URL", "https://openrouter.ai/api/v1"),
            env("OPENROUTER_MODEL", "google/gemma-3-27b-it:free"),
            env("DOCUTRUST_APP_URL", "http://localhost:3000"));
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
