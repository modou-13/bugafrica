package com.bugafrica.answer;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    /** La solution acceptée en premier, puis par ordre chronologique. */
    @EntityGraph(attributePaths = "author")
    List<Answer> findByBugIdOrderByAcceptedDescCreatedAtAsc(Long bugId);

    @Query("""
            select a from Answer a
            join fetch a.bug b
            join fetch b.author
            join fetch a.author
            where a.id = :id
            """)
    Optional<Answer> findDetailedById(@Param("id") Long id);

    Optional<Answer> findByBugIdAndAcceptedTrue(Long bugId);
}
