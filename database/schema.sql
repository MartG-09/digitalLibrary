-- ============================================================================
-- Digital Library Management System - reference schema (MySQL 8)
--
-- You do NOT need to run this: the app creates these tables itself on first
-- start (spring.jpa.hibernate.ddl-auto=update). It is here so you can read the
-- design, or open it in MySQL Workbench and use Database > Reverse Engineer
-- to draw the ER diagram.
--
-- If you do want to create the tables by hand, run this BEFORE the first start.
-- ============================================================================

CREATE DATABASE IF NOT EXISTS digital_library
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE digital_library;

CREATE TABLE users (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    password    VARCHAR(100) NOT NULL,              -- BCrypt hash
    role        VARCHAR(20)  NOT NULL,              -- ADMIN | USER
    enabled     BIT(1)       NOT NULL,
    created_at  DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
);

CREATE TABLE books (
    id                 BIGINT       NOT NULL AUTO_INCREMENT,
    title              VARCHAR(200) NOT NULL,
    author             VARCHAR(150) NOT NULL,
    isbn               VARCHAR(20)  NOT NULL,
    category           VARCHAR(60)  NOT NULL,
    total_quantity     INT          NOT NULL,       -- copies owned
    available_quantity INT          NOT NULL,       -- copies on the shelf
    PRIMARY KEY (id),
    UNIQUE KEY uk_books_isbn (isbn)
);

CREATE TABLE loans (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    book_id       BIGINT        NOT NULL,
    user_id       BIGINT        NOT NULL,
    issue_date    DATE          NOT NULL,
    due_date      DATE          NOT NULL,
    return_date   DATE          NULL,
    status        VARCHAR(20)   NOT NULL,           -- ISSUED | RETURNED
    fine_amount   DECIMAL(10,2) NOT NULL DEFAULT 0,
    fine_status   VARCHAR(20)   NOT NULL,           -- NONE | UNPAID | PAID
    fine_paid_at  DATETIME(6)   NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_loans_book FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT fk_loans_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE reservations (
    id           BIGINT      NOT NULL AUTO_INCREMENT,
    book_id      BIGINT      NOT NULL,
    user_id      BIGINT      NOT NULL,
    reserved_at  DATETIME(6) NOT NULL,
    status       VARCHAR(20) NOT NULL,              -- ACTIVE | FULFILLED | CANCELLED
    PRIMARY KEY (id),
    CONSTRAINT fk_res_book FOREIGN KEY (book_id) REFERENCES books (id),
    CONSTRAINT fk_res_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE contact_messages (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    user_id     BIGINT        NULL,
    name        VARCHAR(100)  NOT NULL,
    email       VARCHAR(150)  NOT NULL,
    subject     VARCHAR(150)  NOT NULL,
    message     VARCHAR(2000) NOT NULL,
    created_at  DATETIME(6)   NOT NULL,
    resolved    BIT(1)        NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_msg_user FOREIGN KEY (user_id) REFERENCES users (id)
);
