package com.bugafrica.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(@NotBlank @Size(max = 120) String email,
                           @NotBlank @Size(max = 72) String password) {
}
