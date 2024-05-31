package com.project2spring.controller.comment;

import com.project2spring.domain.comment.Comment;
import com.project2spring.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService service;

    @PostMapping("add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity addComment(@RequestBody Comment comment, Authentication auth) {
        if (service.validate(comment)) {
            service.add(comment, auth);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("list/{boardId}")
    public List<Comment> list(@PathVariable Integer boardId) {
        return service.list(boardId);
    }

    @DeleteMapping("remove")
    public ResponseEntity remove(@RequestBody Comment comment, Authentication auth) {
        if (service.hasAccess(comment, auth)) {
            service.remove(comment);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("edit")
    public ResponseEntity edit(@RequestBody Comment comment, Authentication auth) {
        if (service.hasAccess(comment, auth)) {
            service.edit(comment, auth);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
