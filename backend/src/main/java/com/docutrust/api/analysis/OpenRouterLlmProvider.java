package com.docutrust.api.analysis;

import com.docutrust.api.document.Document;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnExpression("'${LLM_PROVIDER:mock}' == 'openrouter' || '${LLM_PROVIDER:mock}' == 'auto'")
public class OpenRouterLlmProvider implements LlmProvider {
    private final RestClient client;
    private final ObjectMapper objectMapper;
    private final String model;
    private final boolean configured;

    public OpenRouterLlmProvider(RestClient.Builder builder, ObjectMapper objectMapper,
                                 OpenRouterProperties properties) {
        this.configured = properties.apiKey() != null && !properties.apiKey().isBlank();
        this.client = builder.baseUrl(properties.baseUrl()).defaultHeader("Authorization", "Bearer " + (properties.apiKey() == null ? "" : properties.apiKey()))
            .defaultHeader("HTTP-Referer", properties.appUrl()).defaultHeader("X-Title", "DocuTrust").build();
        this.objectMapper = objectMapper;
        this.model = properties.model();
    }

    @Override
    public List<FindingDraft> analyze(Document document) {
        if (!configured) throw new IllegalStateException("OPENROUTER_API_KEY is not configured");
        String prompt = """
            Analyze the document below for trust, compliance, privacy, and security concerns.
            Return ONLY valid JSON: an array of objects with exactly these string fields:
            category, severity (LOW, MEDIUM, or HIGH), title, explanation.
            If no concern exists, return one LOW finding explaining that no material concern was detected.
            Document file name: %s
            Document text:
            %s
            """.formatted(document.getFileName(), document.getContent());
        JsonNode response = client.post().uri("/chat/completions").contentType(MediaType.APPLICATION_JSON)
            .body(new ChatRequest(model, List.of(new Message("system", "You are a precise document risk analyst."),
                new Message("user", prompt)), 0.1, 1200)).retrieve().body(JsonNode.class);
        if (response == null || response.at("/choices/0/message/content").isMissingNode()) {
            throw new IllegalStateException("OpenRouter returned no completion content");
        }
        return parseFindings(response.at("/choices/0/message/content").asText());
    }

    private List<FindingDraft> parseFindings(String content) {
        try {
            String json = content.trim();
            if (json.startsWith("```")) {
                json = json.replaceFirst("^```(?:json)?\\s*", "").replaceFirst("\\s*```$", "");
            }
            List<FindingDraft> findings = new ArrayList<>();
            for (JsonNode node : objectMapper.readTree(json)) {
                findings.add(new FindingDraft(node.path("category").asText("review"),
                    node.path("severity").asText("MEDIUM").toUpperCase(),
                    node.path("title").asText("Review recommended"),
                    node.path("explanation").asText("The provider returned an incomplete explanation.")));
            }
            if (findings.isEmpty()) throw new IllegalStateException("OpenRouter returned an empty findings array");
            return findings;
        } catch (Exception exception) {
            throw new IllegalStateException("OpenRouter returned invalid findings JSON", exception);
        }
    }

    record OpenRouterProperties(String apiKey, String baseUrl, String model, String appUrl) {}
    record ChatRequest(String model, List<Message> messages, double temperature, int max_tokens) {}
    record Message(String role, String content) {}
}
