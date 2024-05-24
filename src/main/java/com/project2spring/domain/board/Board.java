package com.project2spring.domain.board;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Board {
    private Integer id;
    private String title;
    private String content;
    private String writer; //작성자 nickName 활용
    private Integer memberId;
    private LocalDateTime inserted;

}