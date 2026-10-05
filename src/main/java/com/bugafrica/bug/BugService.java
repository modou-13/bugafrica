package com.bugafrica.bug;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bugafrica.common.ApiException;
import com.bugafrica.security.AuthUser;
import com.bugafrica.tag.Tag;
import com.bugafrica.tag.TagRepository;
import com.bugafrica.user.User;
import com.bugafrica.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BugService {

    private static final int MAX_PAGE_SIZE = 50;

    private final BugRepository bugRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public Page<BugResponse> search(String q, String tag, Boolean solved, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return bugRepository.search(
                q == null ? "" : q.trim(),
                tag == null ? "" : tag.trim().toLowerCase(),
                solved, pageable).map(BugResponse::from);
    }

    @Transactional(readOnly = true)
    public BugResponse get(Long id) {
        return BugResponse.from(find(id));
    }

    @Transactional
    public BugResponse create(BugRequest req, AuthUser authUser) {
        User author = userRepository.findById(authUser.id())
                .orElseThrow(() -> ApiException.unauthorized("Utilisateur introuvable"));
        Bug bug = new Bug();
        bug.setAuthor(author);
        apply(bug, req);
        return BugResponse.from(bugRepository.save(bug));
    }

    @Transactional
    public BugResponse update(Long id, BugRequest req, AuthUser authUser) {
        Bug bug = find(id);
        checkOwnerOrAdmin(bug, authUser);
        apply(bug, req);
        return BugResponse.from(bug);
    }

    @Transactional
    public void delete(Long id, AuthUser authUser) {
        Bug bug = find(id);
        checkOwnerOrAdmin(bug, authUser);
        bugRepository.delete(bug); // réponses et liens de tags supprimés en cascade côté base
    }

    // ---------- interne ----------

    private Bug find(Long id) {
        return bugRepository.findWithAuthorById(id).orElseThrow(() -> ApiException.notFound("Bug introuvable"));
    }

    private void checkOwnerOrAdmin(Bug bug, AuthUser user) {
        if (!bug.getAuthor().getId().equals(user.id()) && !user.isAdmin()) {
            throw ApiException.forbidden("Vous n'avez pas le droit de modifier ce bug");
        }
    }

    private void apply(Bug bug, BugRequest req) {
        bug.setTitle(req.title().trim());
        bug.setDescription(req.description().trim());
        bug.setCodeSnippet(req.codeSnippet() == null || req.codeSnippet().isBlank() ? null : req.codeSnippet());
        bug.setTags(resolveTags(req.tags()));
    }

    /** Réutilise les tags existants et crée les nouveaux (noms normalisés en minuscules). */
    private Set<Tag> resolveTags(List<String> names) {
        Set<Tag> result = new HashSet<>();
        if (names == null || names.isEmpty()) {
            return result;
        }
        Set<String> normalized = names.stream()
                .map(n -> n.trim().toLowerCase())
                .filter(n -> !n.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<String, Tag> existing = tagRepository.findByNameIn(normalized).stream()
                .collect(Collectors.toMap(Tag::getName, Function.identity()));

        for (String name : normalized) {
            result.add(existing.computeIfAbsent(name, n -> tagRepository.save(new Tag(n))));
        }
        return result;
    }
}
