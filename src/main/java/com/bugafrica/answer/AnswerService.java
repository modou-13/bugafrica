package com.bugafrica.answer;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bugafrica.bug.Bug;
import com.bugafrica.bug.BugRepository;
import com.bugafrica.common.ApiException;
import com.bugafrica.security.AuthUser;
import com.bugafrica.user.User;
import com.bugafrica.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnswerService {

    /** Points gagnés par l'auteur d'une réponse acceptée comme solution. */
    private static final int ACCEPTED_REPUTATION = 15;

    private final AnswerRepository answerRepository;
    private final BugRepository bugRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AnswerResponse> list(Long bugId) {
        if (!bugRepository.existsById(bugId)) {
            throw ApiException.notFound("Bug introuvable");
        }
        return answerRepository.findByBugIdOrderByAcceptedDescCreatedAtAsc(bugId)
                .stream().map(AnswerResponse::from).toList();
    }

    @Transactional
    public AnswerResponse create(Long bugId, AnswerRequest req, AuthUser authUser) {
        Bug bug = bugRepository.findById(bugId).orElseThrow(() -> ApiException.notFound("Bug introuvable"));
        User author = userRepository.findById(authUser.id())
                .orElseThrow(() -> ApiException.unauthorized("Utilisateur introuvable"));

        Answer answer = new Answer();
        answer.setBug(bug);
        answer.setAuthor(author);
        answer.setContent(req.content().trim());
        answer.setCodeSnippet(req.codeSnippet() == null || req.codeSnippet().isBlank() ? null : req.codeSnippet());
        return AnswerResponse.from(answerRepository.save(answer));
    }

    /** Seul l'auteur du bug (ou un admin) peut choisir la solution. Une seule solution par bug. */
    @Transactional
    public AnswerResponse accept(Long answerId, AuthUser authUser) {
        Answer answer = answerRepository.findDetailedById(answerId)
                .orElseThrow(() -> ApiException.notFound("Réponse introuvable"));
        Bug bug = answer.getBug();

        if (!bug.getAuthor().getId().equals(authUser.id()) && !authUser.isAdmin()) {
            throw ApiException.forbidden("Seul l'auteur du bug peut accepter une solution");
        }
        if (answer.isAccepted()) {
            return AnswerResponse.from(answer);
        }

        answerRepository.findByBugIdAndAcceptedTrue(bug.getId()).ifPresent(previous -> {
            previous.setAccepted(false);
            giveReputation(previous, bug, -ACCEPTED_REPUTATION);
        });

        answer.setAccepted(true);
        bug.setSolved(true);
        giveReputation(answer, bug, ACCEPTED_REPUTATION);
        return AnswerResponse.from(answer);
    }

    // Pas de points quand on accepte sa propre réponse
    private void giveReputation(Answer answer, Bug bug, int delta) {
        if (!answer.getAuthor().getId().equals(bug.getAuthor().getId())) {
            userRepository.addReputation(answer.getAuthor().getId(), delta);
        }
    }
}
