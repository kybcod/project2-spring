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

ALTER TABLE member
    MODIFY password VARCHAR(200);

SELECT *
FROM member
ORDER BY id;