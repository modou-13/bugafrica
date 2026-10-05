package com.bugafrica.bug;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BugRequest(
        @NotBlank @Size(max = 200)
        String title,

        @NotBlank @Size(max = 10000)
        String description,

        @Size(max = 10000)
        String codeSnippet,

        @Size(max = 5, message = "5 tags maximum")
        List<@Pattern(regexp = "^[a-zA-Z0-9+#._-]{1,40}$", message = "Tag invalide") String> tags) {
}
