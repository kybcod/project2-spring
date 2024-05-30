package com.project2spring.mapper.comment;

import com.project2spring.domain.comment.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Insert("INSERT INTO comment (board_id, member_id, comment) VALUES (#{boardId}, #{memberId}, #{comment})")
    int insert(Comment comment);

    @Select("""
            SELECT c.member_id, c.inserted, c.comment, m.nick_name
            FROM comment c JOIN member m ON c.member_id = m.id
            WHERE c.board_id=#{boardId} 
            ORDER BY c.id
            """)
    List<Comment> selectAllByBoardId(Integer boardId);
}
