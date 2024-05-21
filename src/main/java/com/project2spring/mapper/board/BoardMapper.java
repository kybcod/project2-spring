package com.project2spring.mapper.board;

import com.project2spring.controller.domain.board.Board;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BoardMapper {
    @Insert("""
            INSERT INTO board (title, content, writer)
            VALUES (#{title}, #{content}, #{writer})
            """)
    int insert(Board board);

    @Select("SELECT id, title, writer FROM board ORDER BY id DESC")
    List<Board> selectAll();

    @Select("SELECT * FROM board WHERE id = #{id}")
    Board selectById(Integer id);
}
