package com.bugafrica.bug;

import java.time.Instant;
import java.util.List;

import com.bugafrica.tag.Tag;
import com.bugafrica.user.AuthorSummary;

public record BugResponse(Long id, String title, String description, String codeSnippet,
                          boolean solved, AuthorSummary author, List<String> tags, Instant createdAt) {

    public static BugResponse from(Bug b) {
        return new BugResponse(b.getId(), b.getTitle(), b.getDescription(), b.getCodeSnippet(),
                b.isSolved(), AuthorSummary.from(b.getAuthor()),
                b.getTags().stream().map(Tag::getName).sorted().toList(), b.getCreatedAt());
    }
}
