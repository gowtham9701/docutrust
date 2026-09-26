package com.docutrust.api.analysis;

import com.docutrust.api.document.Document;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;
import java.util.List;

@Component
@Primary
@ConditionalOnProperty(name = "docutrust.llm.provider", havingValue = "auto")
public class RoutingLlmProvider implements LlmProvider {
    private final OpenRouterLlmProvider openRouter;
    private final MockLlmProvider mock;

    public RoutingLlmProvider(OpenRouterLlmProvider openRouter, MockLlmProvider mock) {
        this.openRouter = openRouter;
        this.mock = mock;
    }

    @Override
    public List<FindingDraft> analyze(Document document) {
        try {
            return openRouter.analyze(document);
        } catch (RuntimeException providerFailure) {
            return mock.analyze(document);
        }
    }
}
