CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(30)  NOT NULL,
    email       VARCHAR(120) NOT NULL,
    password    VARCHAR(100) NOT NULL,
    country     VARCHAR(60),
    reputation  INT          NOT NULL DEFAULT 0,
    role        VARCHAR(20)  NOT NULL,
    created_at  DATETIME(6)  NOT NULL,
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tags (
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(40) NOT NULL,
    CONSTRAINT uk_tags_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE bugs (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(200) NOT NULL,
    description   TEXT         NOT NULL,
    code_snippet  TEXT,
    author_id     BIGINT       NOT NULL,
    solved        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    DATETIME(6)  NOT NULL,
    CONSTRAINT fk_bugs_author FOREIGN KEY (author_id) REFERENCES users (id),
    INDEX idx_bugs_created (created_at),
    INDEX idx_bugs_solved_created (solved, created_at),
    INDEX idx_bugs_author (author_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE bug_tags (
    bug_id  BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,
    PRIMARY KEY (bug_id, tag_id),
    CONSTRAINT fk_bug_tags_bug FOREIGN KEY (bug_id) REFERENCES bugs (id) ON DELETE CASCADE,
    CONSTRAINT fk_bug_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE,
    INDEX idx_bug_tags_tag (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE answers (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    bug_id        BIGINT      NOT NULL,
    author_id     BIGINT      NOT NULL,
    content       TEXT        NOT NULL,
    code_snippet  TEXT,
    accepted      BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at    DATETIME(6) NOT NULL,
    CONSTRAINT fk_answers_bug FOREIGN KEY (bug_id) REFERENCES bugs (id) ON DELETE CASCADE,
    CONSTRAINT fk_answers_author FOREIGN KEY (author_id) REFERENCES users (id),
    INDEX idx_answers_bug (bug_id, accepted, created_at),
    INDEX idx_answers_author (author_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
