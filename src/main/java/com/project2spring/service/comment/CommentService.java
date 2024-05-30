package com.project2spring.service.comment;

import com.project2spring.domain.comment.Comment;
import com.project2spring.mapper.comment.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class CommentService {

    final CommentMapper mapper;

    public void add(Comment comment, Authentication auth) {
        comment.setMemberId(Integer.valueOf(auth.getName()));
        mapper.insert(comment);
    }
}
