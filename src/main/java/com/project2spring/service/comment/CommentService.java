package com.project2spring.service.comment;

import com.project2spring.domain.comment.Comment;
import com.project2spring.mapper.comment.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class CommentService {

    final CommentMapper mapper;

    public void add(Comment comment, Authentication auth) {
        comment.setMemberId(Integer.valueOf(auth.getName()));
        mapper.insert(comment);
    }

    public List<Comment> list(Integer boardId) {
        return mapper.selectAllByBoardId(boardId);
    }

    public boolean validate(Comment comment) {
        if (comment == null || comment.getComment().isBlank() || comment.getBoardId() == null) {
            return false;
        }
        return true;
    }

    public void remove(Comment comment) {
        mapper.deleteById(comment.getId());
    }

    public boolean hasAccess(Comment comment, Authentication auth) {
        Comment db = mapper.selectById(comment.getId());
        if (db == null) {
            return false;
        }

        if (!auth.getName().equals(db.getMemberId().toString())) {
            return false;
        }
        return true;
    }

    public void edit(Comment comment) {
        mapper.update(comment);
    }
}
