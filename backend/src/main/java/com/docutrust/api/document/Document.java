package com.docutrust.api.document;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false, length = 10000)
    private String content;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;
    @Column(nullable = false)
    private Instant submittedAt;
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Finding> findings = new ArrayList<>();

    protected Document() {}

    public Document(String fileName, String content) {
        this.id = UUID.randomUUID();
        this.fileName = fileName;
        this.content = content;
        this.status = DocumentStatus.SUBMITTED;
        this.submittedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getFileName() { return fileName; }
    public String getContent() { return content; }
    public DocumentStatus getStatus() { return status; }
    public Instant getSubmittedAt() { return submittedAt; }
    public List<Finding> getFindings() { return findings; }
    public void setStatus(DocumentStatus status) { this.status = status; }
    public void addFinding(Finding finding) { findings.add(finding); finding.setDocument(this); }
}
