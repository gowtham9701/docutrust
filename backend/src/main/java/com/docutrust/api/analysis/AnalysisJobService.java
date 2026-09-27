package com.docutrust.api.analysis;

import com.docutrust.api.audit.AuditEventService;
import com.docutrust.api.document.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

@Service
public class AnalysisJobService {
    private static final Logger log = LoggerFactory.getLogger(AnalysisJobService.class);
    private final DocumentRepository repository;
    private final LlmProvider provider;
    private final AuditEventService auditEvents;

    public AnalysisJobService(DocumentRepository repository, LlmProvider provider, AuditEventService auditEvents) {
        this.repository = repository;
        this.provider = provider;
        this.auditEvents = auditEvents;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("analysisExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void analyze(AnalysisRequestedEvent event) {
        UUID id = event.documentId();
        Document document = repository.findById(id).orElseThrow(() -> new IllegalStateException("Document not found: " + id));
        try {
            provider.analyze(document).forEach(draft -> document.addFinding(
                new Finding(draft.category(), draft.severity(), draft.title(), draft.explanation())));
            document.setStatus(DocumentStatus.REVIEW);
            auditEvents.record("ANALYSIS_COMPLETED", id);
        } catch (RuntimeException exception) {
            document.setStatus(DocumentStatus.FAILED);
            auditEvents.record("ANALYSIS_FAILED", id);
            log.error("Document analysis failed for document {}", id, exception);
        }
        repository.save(document);
    }
}
