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

SELECT *
FROM member;

INSERT INTO member (email, password, nick_name)
VALUES ('222222@222222', 123, 222222);
INSERT INTO member (email, password, nick_name)
VALUES ('333333@333333', 123, 333333);
INSERT INTO member (email, password, nick_name)
VALUES ('444444@444444', 123, 444444);
INSERT INTO member (email, password, nick_name)
VALUES ('555555@555555', 123, 555555);
INSERT INTO member (email, password, nick_name)
VALUES ('666666@666666', 123, 666666);
INSERT INTO member (email, password, nick_name)
VALUES ('777777@777777', 123, 777777);
INSERT INTO member (email, password, nick_name)
VALUES ('888888@888888', 123, 888888);
INSERT INTO member (email, password, nick_name)
VALUES ('999999@999999', 123, 999999);
INSERT INTO member (email, password, nick_name)
VALUES ('111111@111111', 123, 111111);

SELECT *
FROM member
WHERE nick_name LIKE '%1%';

SELECT COUNT(*)
FROM member
WHERE email LIKE '%3%';

CREATE TABLE board_file
(
    board_id INT          NOT NULL REFERENCES board (id),
    name     VARCHAR(500) NOT NULL,
    PRIMARY KEY (board_id, name)
);

SELECT *
FROM board_file
WHERE board_file.board_id = 955;


SELECT id, email, password, nick_name, inserted
FROM member
WHERE email LIKE '%111%'
   OR nick_name LIKE '%111%'
ORDER BY id DESC;
# LIMIT 10, 10;


SELECT COUNT(*)
FROM member
WHERE email LIKE '%111%';

CREATE TABLE board_like
(
    board_id  INT NOT NULL REFERENCES board (id),
    member_id INT NOT NULL REFERENCES member (id),
    PRIMARY KEY (board_id, member_id)
);

SELECT *
FROM board_like;

USE prj2;
SELECT b.id, COUNT(DISTINCT f.name), COUNT(DISTINCT ,l.member_id)
FROM board b
         JOIN member m ON b.member_id = m.id
         LEFT JOIN board_file f on b.id = f.board_id
         LEFT JOIN board_like l on b.id = l.board_id
WHERE b.id = 1;