package com.docutrust.api.audit;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events", indexes = @Index(name = "idx_audit_document", columnList = "document_id"))
public class AuditEvent {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String eventType;
    private UUID documentId;
    @Column(nullable = false)
    private Instant occurredAt;
    @Column(nullable = false)
    private String actor;

    protected AuditEvent() {}
    public AuditEvent(String eventType, UUID documentId, String actor) {
        this.id = UUID.randomUUID();
        this.eventType = eventType;
        this.documentId = documentId;
        this.actor = actor;
        this.occurredAt = Instant.now();
    }
    public UUID getId() { return id; }
    public String getEventType() { return eventType; }
    public UUID getDocumentId() { return documentId; }
    public Instant getOccurredAt() { return occurredAt; }
    public String getActor() { return actor; }
}
