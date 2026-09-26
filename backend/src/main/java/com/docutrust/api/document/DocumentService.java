package com.docutrust.api.document;

import com.docutrust.api.analysis.AnalysisJobService;
import com.docutrust.api.audit.AuditEvent;
import com.docutrust.api.audit.AuditEventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {
    private final DocumentRepository repository;
    private final AuditEventService auditEvents;
    private final AnalysisJobService analysisJobs;

    public DocumentService(DocumentRepository repository, AuditEventService auditEvents,
                           AnalysisJobService analysisJobs) {
        this.repository = repository;
        this.auditEvents = auditEvents;
        this.analysisJobs = analysisJobs;
    }

    @Transactional
    public DocumentDtos.DocumentResponse submit(DocumentDtos.SubmitRequest request) {
        Document document = repository.save(new Document(request.fileName(), request.content()));
        auditEvents.record("DOCUMENT_SUBMITTED", document.getId());
        document.setStatus(DocumentStatus.ANALYZING);
        Document saved = repository.save(document);
        analysisJobs.analyze(saved.getId());
        return DocumentDtos.DocumentResponse.from(saved);
    }

    @Transactional
    public DocumentDtos.DocumentResponse submitUpload(String fileName, String contentType, byte[] bytes) {
        if (bytes.length == 0) throw new IllegalArgumentException("Uploaded file is empty");
        if (bytes.length > 5 * 1024 * 1024) throw new IllegalArgumentException("Uploaded file exceeds 5MB limit");
        if (contentType != null && !(contentType.startsWith("text/") || contentType.equals("application/json"))) {
            throw new IllegalArgumentException("Only text and JSON documents are supported in this release");
        }
        return submit(new DocumentDtos.SubmitRequest(fileName, new String(bytes, StandardCharsets.UTF_8)));
    }

    @Transactional(readOnly = true)
    public DocumentDtos.DocumentResponse get(UUID id) {
        return repository.findById(id).map(DocumentDtos.DocumentResponse::from)
            .orElseThrow(() -> new DocumentNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<DocumentDtos.DocumentResponse> findAll() {
        return repository.findAll().stream().map(DocumentDtos.DocumentResponse::from).toList();
    }

    public static class DocumentNotFoundException extends RuntimeException {
        public DocumentNotFoundException(UUID id) { super("Document not found: " + id); }
    }
}
