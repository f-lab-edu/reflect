-- V1: Reflect 애플리케이션 초기 테이블 생성

-- 회원 테이블 생성
CREATE TABLE member (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(512) NOT NULL,
    name VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- 질문 카테고리 테이블 생성
CREATE TABLE question_category (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- 질문 테이블 생성
CREATE TABLE question (
    question BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    question_category_id BIGINT NOT NULL,
    content VARCHAR(200) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_question_category FOREIGN KEY (question_category_id) REFERENCES question_category(id)
);

-- 질문 답변 테이블 생성
CREATE TABLE question_answer (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    content LONGTEXT NOT NULL,
    is_private BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_question_answer_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT fk_question_answer_question FOREIGN KEY (question_id) REFERENCES question(question)
);

-- 질문 답변 좋아요 테이블 생성
CREATE TABLE question_answer_like (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    question_answer_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_question_answer_like_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT fk_question_answer_like_answer FOREIGN KEY (question_answer_id) REFERENCES question_answer(id)
);