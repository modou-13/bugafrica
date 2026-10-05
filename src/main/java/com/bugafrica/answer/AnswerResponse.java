package com.bugafrica.answer;

import java.time.Instant;

import com.bugafrica.user.AuthorSummary;

public record AnswerResponse(Long id, Long bugId, String content, String codeSnippet,
                             boolean accepted, AuthorSummary author, Instant createdAt) {

    public static AnswerResponse from(Answer a) {
        return new AnswerResponse(a.getId(), a.getBug().getId(), a.getContent(), a.getCodeSnippet(),
                a.isAccepted(), AuthorSummary.from(a.getAuthor()), a.getCreatedAt());
    }
}
