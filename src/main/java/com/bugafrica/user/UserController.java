package com.bugafrica.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugafrica.common.ApiException;
import com.bugafrica.security.AuthUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public UserResponse me(@AuthenticationPrincipal AuthUser authUser) {
        return userRepository.findById(authUser.id())
                .map(UserResponse::from)
                .orElseThrow(() -> ApiException.unauthorized("Utilisateur introuvable"));
    }
}
