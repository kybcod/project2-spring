package com.project2spring.mapper.member;

import com.project2spring.domain.Member;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    @Insert("""
            insert into member (email, password, nick_name)
            values (#{email}, #{password}, #{nickName})
            """)
    Member insert(Member member);
}
