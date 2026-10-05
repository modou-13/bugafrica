package com.bugafrica.answer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnswerRequest(@NotBlank @Size(max = 10000) String content,
                            @Size(max = 10000) String codeSnippet) {
}
