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
