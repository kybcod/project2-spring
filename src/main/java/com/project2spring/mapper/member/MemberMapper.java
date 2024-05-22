package com.project2spring.mapper.member;

import com.project2spring.domain.member.Member;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    @Insert("INSERT INTO member (email, password, nick_name) VALUES (#{email}, #{password}, #{nickName})")
    int insert(Member member);
}
