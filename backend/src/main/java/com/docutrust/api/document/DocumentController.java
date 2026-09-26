package com.docutrust.api.document;

import com.docutrust.api.audit.AuditEvent;
import com.docutrust.api.audit.AuditEventService;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {
    private final DocumentService service;
    private final AuditEventService auditEvents;
    public DocumentController(DocumentService service, AuditEventService auditEvents) {
        this.service = service;
        this.auditEvents = auditEvents;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentDtos.DocumentResponse submit(@Valid @RequestBody DocumentDtos.SubmitRequest request) {
        return service.submit(request);
    }

    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentDtos.DocumentResponse upload(@RequestPart("file") MultipartFile file) {
        try {
            return service.submitUpload(file.getOriginalFilename(), file.getContentType(), file.getBytes());
        } catch (java.io.IOException exception) {
            throw new IllegalArgumentException("Could not read uploaded document", exception);
        }
    }

    @GetMapping
    public List<DocumentDtos.DocumentResponse> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public DocumentDtos.DocumentResponse get(@PathVariable UUID id) { return service.get(id); }

    @GetMapping("/{id}/status")
    public DocumentStatus status(@PathVariable UUID id) { return service.get(id).status(); }

    @GetMapping("/{id}/findings")
    public List<DocumentDtos.FindingResponse> findings(@PathVariable UUID id) { return service.get(id).findings(); }

    @GetMapping("/{id}/audit")
    public List<AuditEvent> audit(@PathVariable UUID id) {
        service.get(id);
        return auditEvents.findForDocument(id);
    }
}
