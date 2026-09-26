package com.docutrust.api.analysis;

import com.docutrust.api.document.Document;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnExpression("'${LLM_PROVIDER:mock}' == 'mock' || '${LLM_PROVIDER:mock}' == 'auto'")
public class MockLlmProvider implements LlmProvider {
    @Override
    public List<FindingDraft> analyze(Document document) {
        if (document.getContent() == null || document.getContent().isBlank()) {
            return List.of(new FindingDraft("completeness", "MEDIUM", "Document has no extractable text",
                "Add text content or connect a document extraction pipeline before analysis."));
        }
        return List.of(new FindingDraft("review", "LOW", "Human review recommended",
            "Mock analysis completed. Replace this provider with a configured LLM for production checks."));
    }
}
