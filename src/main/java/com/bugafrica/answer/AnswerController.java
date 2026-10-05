package com.bugafrica.answer;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.bugafrica.security.AuthUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @GetMapping("/api/bugs/{bugId}/answers")
    public List<AnswerResponse> list(@PathVariable Long bugId) {
        return answerService.list(bugId);
    }

    @PostMapping("/api/bugs/{bugId}/answers")
    @ResponseStatus(HttpStatus.CREATED)
    public AnswerResponse create(@PathVariable Long bugId, @Valid @RequestBody AnswerRequest request,
                                 @AuthenticationPrincipal AuthUser user) {
        return answerService.create(bugId, request, user);
    }

    @PostMapping("/api/answers/{id}/accept")
    public AnswerResponse accept(@PathVariable Long id, @AuthenticationPrincipal AuthUser user) {
        return answerService.accept(id, user);
    }
}
