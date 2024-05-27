package com.project2spring.mapper.member;

import com.project2spring.domain.member.Member;
import org.apache.ibatis.annotations.*;

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

    @Update("UPDATE member SET password=#{password}, nick_name=#{nickName} WHERE id=#{id}")
    int update(Member member);

    @Select("SELECT name FROM authority WHERE member_id = #{memberId}")
    List<String> selectAuthorityByMemberId(Integer memberId);

    @Select("""
            <script>
            SELECT id, email, password, nick_name, inserted
            FROM member
            <trim prefix="WHERE" prefixOverrides="OR">
                <if test="type != null">
                    <bind name="pattern" value="'%' + keyword + '%'" />
                    <if test="type == 'all' || type == 'email'">
                        OR email LIKE #{pattern}
                    </if>
                     <if test="type == 'all' || type == 'nick_name'">
                        OR nick_name LIKE #{pattern}
                    </if>
                </if>
            </trim>
            ORDER BY id DESC
            LIMIT #{offset}, 10
            </script>
            """)
    List<Member> selectAllPaging(Integer offset, String type, String keyword);

    @Select("SELECT COUNT(*) FROM member")
    Integer countAll();
}
