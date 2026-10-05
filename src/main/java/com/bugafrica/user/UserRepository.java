package com.bugafrica.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    /** Mise à jour atomique côté base : pas de condition de concurrence sur la réputation. */
    @Modifying
    @Query("update User u set u.reputation = u.reputation + :delta where u.id = :id")
    void addReputation(@Param("id") Long id, @Param("delta") int delta);
}
