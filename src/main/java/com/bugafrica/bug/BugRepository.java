package com.bugafrica.bug;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BugRepository extends JpaRepository<Bug, Long> {

    /** Recherche paginée. q et tag valent "" quand le filtre est absent, solved vaut null. */
    @EntityGraph(attributePaths = "author")
    @Query("""
            select b from Bug b
            where (:solved is null or b.solved = :solved)
              and (:tag = '' or exists (select t.id from b.tags t where t.name = :tag))
              and (:q = '' or lower(b.title) like lower(concat('%', :q, '%'))
                           or lower(b.description) like lower(concat('%', :q, '%')))
            """)
    Page<Bug> search(@Param("q") String q, @Param("tag") String tag,
                     @Param("solved") Boolean solved, Pageable pageable);

    @EntityGraph(attributePaths = "author")
    Optional<Bug> findWithAuthorById(Long id);
}
