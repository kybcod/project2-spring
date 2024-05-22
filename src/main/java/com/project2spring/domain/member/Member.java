package com.project2spring.domain.member;

import lombok.Data;

@Data
public class Member {
    private Integer id;
    private String email;
    private String password;
    private String nickName;
}
