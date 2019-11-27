package com.problemone.commentservice.web;

import com.problemone.commentservice.model.Comment;
import com.problemone.commentservice.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/")
    public List<Comment> getComments(){
        return this.commentService.findAll();
    }

    @GetMapping("/{id}")
    public Comment getCommentById(@PathVariable("id") String commentId) {
        try {
            return this.commentService.findById(commentId);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "The comment was not found!"
            );
        }
    }

    @PostMapping("/")
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        Comment created = this.commentService.add(comment);
        return ResponseEntity.created(
            ServletUriComponentsBuilder.fromCurrentRequest().pathSegment("{id}").build(created.getId()))
            .body(created);
    }

    @PutMapping("/{id}")
    public Comment updateComment(@PathVariable("id") String commentId, @RequestBody Comment comment) throws Exception {
        if(!commentId.equals(comment.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "The ids does not match!"
            );
        }
        return this.commentService.update(comment);
    }

    @DeleteMapping("{id}")
    public Comment deleteComment(@PathVariable("id") String commentId) {
        try {
            return this.commentService.delete(commentId);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "The comment was not found!"
            );
        }
    }

}
