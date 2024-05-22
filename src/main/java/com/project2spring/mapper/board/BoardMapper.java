package com.project2spring.mapper.board;

import com.project2spring.controller.domain.board.Board;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {
    @Insert("""
            INSERT INTO board (title, content, writer)
            VALUES (#{title}, #{content}, #{writer})
            """)
    int insert(Board board);

    @Select("SELECT * FROM board")
    List<Board> selectAll();

    @Select("SELECT * FROM board WHERE id=#{id}")
    Board selectById(Integer id);

    @Delete("DELETE FROM board WHERE id=#{id}")
    void deleteById(Integer id);

    @Update("""
            UPDATE board
            SET title=#{title}, content=#{content}, writer=#{writer}
            WHERE id=#{id}
            """)
    void update(Board board);
}
