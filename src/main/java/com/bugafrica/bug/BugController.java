package com.bugafrica.bug;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.bugafrica.security.AuthUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bugs")
@RequiredArgsConstructor
public class BugController {

    private final BugService bugService;

    @GetMapping
    public Page<BugResponse> search(@RequestParam(required = false) String q,
                                    @RequestParam(required = false) String tag,
                                    @RequestParam(required = false) Boolean solved,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        return bugService.search(q, tag, solved, page, size);
    }

    @GetMapping("/{id}")
    public BugResponse get(@PathVariable Long id) {
        return bugService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BugResponse create(@Valid @RequestBody BugRequest request, @AuthenticationPrincipal AuthUser user) {
        return bugService.create(request, user);
    }

    @PutMapping("/{id}")
    public BugResponse update(@PathVariable Long id, @Valid @RequestBody BugRequest request,
                              @AuthenticationPrincipal AuthUser user) {
        return bugService.update(id, request, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal AuthUser user) {
        bugService.delete(id, user);
    }
}
