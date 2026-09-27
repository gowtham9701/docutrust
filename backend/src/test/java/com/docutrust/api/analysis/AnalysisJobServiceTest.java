package com.docutrust.api.analysis;

import com.docutrust.api.audit.AuditEventService;
import com.docutrust.api.document.Document;
import com.docutrust.api.document.DocumentRepository;
import com.docutrust.api.document.DocumentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AnalysisJobServiceTest {
    private DocumentRepository repository;
    private LlmProvider provider;
    private AuditEventService auditEvents;
    private AnalysisJobService service;
    private Document document;

    @BeforeEach
    void setUp() {
        repository = mock(DocumentRepository.class);
        provider = mock(LlmProvider.class);
        auditEvents = mock(AuditEventService.class);
        service = new AnalysisJobService(repository, provider, auditEvents);
        document = new Document("policy.txt", "Protect customer information.");
        document.setStatus(DocumentStatus.ANALYZING);
        when(repository.findById(document.getId())).thenReturn(Optional.of(document));
    }

    @Test
    void analysisCompletionPersistsFindingsAndReviewStatus() {
        when(provider.analyze(document)).thenReturn(List.of(
            new LlmProvider.FindingDraft("privacy", "MEDIUM", "Review data safeguards", "Check the safeguards.")));

        service.analyze(new AnalysisRequestedEvent(document.getId()));

        assertEquals(DocumentStatus.REVIEW, document.getStatus());
        assertEquals(1, document.getFindings().size());
        verify(auditEvents).record("ANALYSIS_COMPLETED", document.getId());
        verify(repository).save(document);
    }

    @Test
    void analysisFailurePersistsFailedStatusAndAuditEvent() {
        when(provider.analyze(document)).thenThrow(new IllegalStateException("provider unavailable"));

        service.analyze(new AnalysisRequestedEvent(document.getId()));

        assertEquals(DocumentStatus.FAILED, document.getStatus());
        verify(auditEvents).record("ANALYSIS_FAILED", document.getId());
        verify(repository).save(document);
    }
}
