package com.docutrust.api.document;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "findings")
public class Finding {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;
    @Column(nullable = false)
    private String category;
    @Column(nullable = false)
    private String severity;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, length = 4000)
    private String explanation;

    protected Finding() {}
    public Finding(String category, String severity, String title, String explanation) {
        this.id = UUID.randomUUID();
        this.category = category;
        this.severity = severity;
        this.title = title;
        this.explanation = explanation;
    }
    public UUID getId() { return id; }
    public String getCategory() { return category; }
    public String getSeverity() { return severity; }
    public String getTitle() { return title; }
    public String getExplanation() { return explanation; }
    void setDocument(Document document) { this.document = document; }
}
