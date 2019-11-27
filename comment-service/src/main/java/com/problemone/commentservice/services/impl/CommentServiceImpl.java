package com.problemone.commentservice.services.impl;

import com.problemone.commentservice.model.Comment;
import com.problemone.commentservice.repository.CommentRepository;
import com.problemone.commentservice.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<Comment> findAll() {
        return this.commentRepository.findAll();
    }

    @Override
    public Comment findById(String commentId) throws Exception {
        return this.commentRepository.findById(commentId).orElseThrow(() -> new Exception("The comment was not found!"));
    }

    @Override
    public Comment add(Comment comment) {
        return this.commentRepository.save(comment);
    }

    @Override
    public Comment update(Comment comment) throws Exception {
        Optional<Comment> oldComment = this.commentRepository.findById(comment.getId());
        if(!oldComment.isPresent()) {
            throw new Exception("The comment does not exist!");
        }
        comment.setCreated(oldComment.get().getCreated());
        comment.setModified(LocalDateTime.now());
        return this.commentRepository.save(comment);
    }

    @Override
    public Comment delete(String commentId) throws Exception {
        Optional<Comment> comment = this.commentRepository.findById(commentId);
        if(!comment.isPresent()) {
            throw new Exception("The comment does not exist!");
        }
        this.commentRepository.deleteById(commentId);
        return comment.get();
    }
}
