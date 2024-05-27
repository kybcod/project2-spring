USE prj2;
CREATE TABLE board
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    title    VARCHAR(100)  NOT NULL,
    content  VARCHAR(1000) NOT NULL,
    writer   VARCHAR(100)  NOT NULL,
    inserted DATETIME      NOT NULL DEFAULT NOW()
);

SELECT *
FROM board
ORDER BY id;

CREATE TABLE member
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    email     VARCHAR(100) NOT NULL UNIQUE,
    password  VARCHAR(30)  NOT NULL,
    nick_name VARCHAR(20)  NOT NULL UNIQUE,
    inserted  DATETIME     NOT NULL DEFAULT now()
);

ALTER TABLE board
    drop column writer;

ALTER TABLE board
    add column member_id int REFERENCES member (id) AFTER content;

ALTER TABLE board
    MODIFY COLUMN member_id INT NOT NULL;

UPDATE board
SET member_id = (SELECT id FROM member ORDER BY id DESC LIMIT 1)
WHERE id > 0;

SELECT *
FROM member
ORDER BY id;

SELECT *
FROM member
WHERE id = 23;

DELETE
FROM board
WHERE member_id = 23;

DELETE
FROM member
WHERE id = 23;

DESC board;

## 권한 테이블
CREATE TABLE authority
(
    member_id INT         NOT NULL REFERENCES member (id),
    name      VARCHAR(20) NOT NULL,
    PRIMARY KEY (member_id, name)
);

INSERT INTO authority (member_id, name)
VALUES (23, 'admin');

# 게시물 여러 개 입력
INSERT INTO board (title, content, member_id)
SELECT title, content, member_id
FROM board;

SELECT *
FROM member;

UPDATE member
SET nick_name = 'abcd'
WHERE id = 31;

UPDATE member
SET nick_name = 'efgh'
WHERE id = 34;

UPDATE board
SET member_id = 31
WHERE id % 2 = 1;

UPDATE board
SET member_id = 34
WHERE id % 2 = 0;

UPDATE board
SET title   = 'abc def',
    content = 'ghi jkl'
WHERE id % 3 = 0;

UPDATE board
SET title   = 'mno pgq',
    content = 'stu vwx'
WHERE id % 3 = 1;

UPDATE board
SET title   = 'yz1 234',
    content = '567 890'
WHERE id % 3 = 2;

