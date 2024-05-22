package com.project2spring.mapper.member;

import com.project2spring.domain.member.Member;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MemberMapper {

    @Insert("INSERT INTO member (email, password, nick_name) VALUES (#{email}, #{password}, #{nickName})")
    int insert(Member member);

    @Select("SELECT * FROM member WHERE email = #{email}")
    Member selectByEmail(String email);

    @Select("SELECT * FROM member WHERE nick_name = #{nickName}")
    Member selectByNickName(String nickName);

    @Select("SELECT * FROM member")
    List<Member> selectAll();

    @Select("SELECT * FROM member WHERE id = #{id}")
    Member selectById(Integer id);

    @Delete("DELETE FROM member WHERE id=#{id}")
    int deleteById(Integer id);
}