package com.project2spring.mapper.board;

import com.project2spring.controller.domain.board.Board;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {
    @Insert("""
            INSERT INTO board
            VALUES (#{title}, #{content}, #{writer})
            """)
    int insert(Board board);
}
