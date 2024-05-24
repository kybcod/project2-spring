package com.project2spring.mapper.member;

import com.project2spring.domain.Member;
import org.apache.ibatis.annotations.*;

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

    @Delete("DELETE FROM member WHERE id=${id}")
    void deleteById(Integer id);

    @Update("UPDATE member SET password=#{password}, nick_name=#{nickName} WHERE id=#{id}")
    void update(Member member);
}
