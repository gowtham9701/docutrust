package com.docutrust.api.audit;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class AuditEventService {
    private final AuditEventRepository repository;
    public AuditEventService(AuditEventRepository repository) { this.repository = repository; }
    public void record(String type, UUID documentId) {
        repository.save(new AuditEvent(type, documentId, "system"));
    }
    public List<AuditEvent> findForDocument(UUID documentId) {
        return repository.findByDocumentIdOrderByOccurredAtAsc(documentId);
    }
}
