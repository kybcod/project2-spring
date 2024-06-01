package com.project2spring.domain.member;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class Member {

    private Integer id;
    private String email;
    private String password;
    private String oldPassword;
    private String nickName;
    private LocalDateTime inserted;
    private String profile; //파일이름
    private String awsProfile; //aws+파일이름


    public String getSignupDateAndTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        return inserted.format(formatter);
    }

    public void setAwsProfile(String srcPrefix) {
        if (profile == null || profile.isBlank()) {
            this.awsProfile = STR."\{srcPrefix}prj/default/profile.jpg";
        } else {
            this.awsProfile = STR."\{srcPrefix}prj/\{id}/\{profile}";
        }
    }
}
