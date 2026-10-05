package com.bugafrica.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bugafrica.common.ApiException;
import com.bugafrica.security.JwtService;
import com.bugafrica.user.User;
import com.bugafrica.user.UserRepository;
import com.bugafrica.user.UserResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        String username = req.username().trim();

        if (userRepository.existsByEmail(email)) {
            throw ApiException.conflict("Cet email est déjà utilisé");
        }
        if (userRepository.existsByUsername(username)) {
            throw ApiException.conflict("Ce pseudo est déjà pris");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setCountry(req.country() == null || req.country().isBlank() ? null : req.country().trim());
        userRepository.save(user);

        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email().trim().toLowerCase())
                .filter(u -> passwordEncoder.matches(req.password(), u.getPassword()))
                // Message volontairement identique que l'email existe ou non
                .orElseThrow(() -> ApiException.unauthorized("Email ou mot de passe incorrect"));
        return toResponse(user);
    }

    private AuthResponse toResponse(User user) {
        return new AuthResponse(jwtService.generateToken(user.getUsername()), "Bearer", UserResponse.from(user));
    }
}
