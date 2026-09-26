package com.docutrust.api.analysis;

import com.docutrust.api.document.Document;

import java.util.List;

public interface LlmProvider {
    List<FindingDraft> analyze(Document document);

    record FindingDraft(String category, String severity, String title, String explanation) {}
}
