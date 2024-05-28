package com.project2spring.domain.member;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class Member {
    private Integer id;
    private String email;
    private String password;
    private String oldPassword;
    private String nickName;
    private LocalDateTime inserted;
    private List<MemberFile> file;


    public String getSignupDateAndTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        return inserted.format(formatter);
    }
}
