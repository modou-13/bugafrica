package com.bugafrica.auth;

import com.bugafrica.user.UserResponse;

public record AuthResponse(String token, String tokenType, UserResponse user) {
}
