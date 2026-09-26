package com.docutrust.api.document;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class DocumentDtos {
    private DocumentDtos() {}
    public record SubmitRequest(@NotBlank String fileName, @NotBlank String content) {}
    public record FindingResponse(UUID id, String category, String severity, String title, String explanation) {
        static FindingResponse from(Finding finding) {
            return new FindingResponse(finding.getId(), finding.getCategory(), finding.getSeverity(),
                finding.getTitle(), finding.getExplanation());
        }
    }
    public record DocumentResponse(UUID id, String fileName, DocumentStatus status, Instant submittedAt,
                                   List<FindingResponse> findings) {
        static DocumentResponse from(Document document) {
            return new DocumentResponse(document.getId(), document.getFileName(), document.getStatus(),
                document.getSubmittedAt(), document.getFindings().stream().map(FindingResponse::from).toList());
        }
    }
}
