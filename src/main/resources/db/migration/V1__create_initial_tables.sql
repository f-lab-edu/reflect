-- V1: Reflect 애플리케이션 초기 테이블 생성

-- 회원 테이블 생성
CREATE TABLE member (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(512) NOT NULL,
    name VARCHAR(20) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- 질문 카테고리 테이블 생성
CREATE TABLE question_category (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- 질문 테이블 생성
CREATE TABLE question (
    question BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    question_category_id BIGINT NOT NULL,
    content VARCHAR(200) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
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
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_question_answer_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT fk_question_answer_question FOREIGN KEY (question_id) REFERENCES question(question)
);

-- 질문 답변 좋아요 테이블 생성
CREATE TABLE question_answer_like (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    question_answer_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_question_answer_like_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT fk_question_answer_like_answer FOREIGN KEY (question_answer_id) REFERENCES question_answer(id)
);

-- 성능 향상을 위한 인덱스 생성
CREATE INDEX idx_member_email ON member(email);
CREATE INDEX idx_question_category ON question(question_category_id);
CREATE INDEX idx_question_answer_member ON question_answer(member_id);
CREATE INDEX idx_question_answer_question ON question_answer(question_id);
CREATE INDEX idx_question_answer_like_member ON question_answer_like(member_id);
CREATE INDEX idx_question_answer_like_answer ON question_answer_like(question_answer_id);

-- 중복 좋아요 방지를 위한 유니크 제약조건 추가
ALTER TABLE question_answer_like ADD CONSTRAINT uk_member_answer_like UNIQUE (member_id, question_answer_id);
