package com.project2spring.controller.board;

import com.project2spring.controller.domain.board.Board;
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

    @PostMapping("add")
    public ResponseEntity add(@RequestBody Board board) throws InterruptedException {
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

    // /api/board/5
    @GetMapping("{id}")
    public ResponseEntity get(@PathVariable Integer id) {
        Board board = service.get(id);

        if (board == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(board);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        service.remove(id);
    }

    @PutMapping("edit")
    public void edit(@RequestBody Board board) {
        service.edit(board);
    }
}
