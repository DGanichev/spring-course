package com.problemone.commentservice.services;

import com.problemone.commentservice.model.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> findAll();
    Comment findById(String commentId) throws Exception;
    Comment add(Comment comment);
    Comment update(Comment comment) throws Exception;
    Comment delete(String commentId) throws Exception;
}
