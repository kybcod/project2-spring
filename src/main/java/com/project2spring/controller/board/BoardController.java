package com.project2spring.controller.board;

import com.project2spring.domain.Board;
import com.project2spring.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService service;

    @PostMapping("insert")
    public ResponseEntity add(@RequestBody Board board) {
        if (service.validate(board)) {
            service.add(board);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("list")
    public List<Board> list() {
        return service.list();
    }

    @GetMapping("{id}")
    public ResponseEntity get(@PathVariable Integer id) {
        Board board = service.getById(id);
        if (board == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().body(board);
        }
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @PutMapping("edit")
    public ResponseEntity update(@RequestBody Board board) {
        if (service.validate(board)) {
            service.update(board);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


}
