package com.project2spring.mapper.comment;

import com.project2spring.domain.comment.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {

    @Insert("INSERT INTO comment (board_id, member_id, comment) VALUES (#{boardId}, #{memberId}, #{comment})")
    int insert(Comment comment);
}
