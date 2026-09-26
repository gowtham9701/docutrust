package com.docutrust.api.analysis;

import com.docutrust.api.audit.AuditEventService;
import com.docutrust.api.document.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class AnalysisJobService {
    private final DocumentRepository repository;
    private final LlmProvider provider;
    private final AuditEventService auditEvents;

    public AnalysisJobService(DocumentRepository repository, LlmProvider provider, AuditEventService auditEvents) {
        this.repository = repository;
        this.provider = provider;
        this.auditEvents = auditEvents;
    }

    @Async("analysisExecutor")
    @Transactional
    public void analyze(UUID id) {
        Document document = repository.findById(id).orElseThrow();
        try {
            provider.analyze(document).forEach(draft -> document.addFinding(
                new Finding(draft.category(), draft.severity(), draft.title(), draft.explanation())));
            document.setStatus(DocumentStatus.REVIEW);
            auditEvents.record("ANALYSIS_COMPLETED", id);
        } catch (RuntimeException exception) {
            document.setStatus(DocumentStatus.FAILED);
            auditEvents.record("ANALYSIS_FAILED", id);
        }
        repository.save(document);
    }
}
