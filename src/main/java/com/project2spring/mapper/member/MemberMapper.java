package com.project2spring.mapper.member;

import com.project2spring.domain.Member;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MemberMapper {

    @Insert("""
            insert into member (email, password, nick_name)
            values (#{email}, #{password}, #{nickName})
            """)
    int insert(Member member);

    @Select("""
            SELECT *
            FROM member
            WHERE email = #{email}
            """)
    Member selectByEmail(String email);

    @Select("""
            SELECT *
            FROM member
            WHERE nick_name = #{nickName}
            """)
    Member selectByNicKName(String nickName);

    @Select("SELECT * FROM member")
    List<Member> selectAll();

    @Select("SELECT * FROM member WHERE id = ${id}")
    Member selectById(Integer id);
}
