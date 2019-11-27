package com.homework.one.blogger.web;

import com.homework.one.blogger.domain.entity.Post;
import com.homework.one.blogger.domain.entity.User;
import com.homework.one.blogger.domain.model.PostCreateRequest;
import com.homework.one.blogger.service.PostService;
import com.homework.one.blogger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity getPosts() {
        List<Post> posts = this.postService.getAllPosts();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity getPost(@PathVariable("postId") String postId) {
        Optional<Post> post = this.postService.findPostById(postId);
        if(!post.isPresent()) {
            return new ResponseEntity<>("The post was not found!", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(post.get(), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity createPost(@RequestBody PostCreateRequest postReq, HttpServletRequest request) {
        Post post = new Post();
        post.setTitle(postReq.getTitle());
        post.setAuthor(request.getUserPrincipal().getName());
        post.setText(postReq.getText());
        post.setTags(postReq.getTags());
        post.setUrl(postReq.getUrl());
        post.setActive(postReq.isActive());
        this.postService.savePost(post);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.
                        fromCurrentRequest().
                        pathSegment("{id}").
                        build(post.getId()))
                .body(post);
    }

    @PutMapping("/{postId}")
    public ResponseEntity editPost(@PathVariable("postId") String postId, @RequestBody PostCreateRequest postReq, HttpServletRequest request) {
        if(!postId.equals(postReq.getPostId())) {
            return new ResponseEntity("The ids does not match.", HttpStatus.BAD_REQUEST);
        }
        Optional<Post> post = this.postService.findPostById(postId);
        if(!post.isPresent()) {
            return new ResponseEntity("The post was not found", HttpStatus.NOT_FOUND);
        }
        if(request.isUserInRole("BLOGGER") && !isAuthorTheCurrentUser(post, request)) {
            return new ResponseEntity("You have no permission to edit other user's posts.", HttpStatus.FORBIDDEN);
        }
        Post newPost = new Post();
        newPost.setId(postReq.getPostId());
        newPost.setTitle(postReq.getTitle());
        newPost.setText(postReq.getText());
        newPost.setTags(postReq.getTags());
        newPost.setUrl(postReq.getUrl());
        newPost.setActive(postReq.isActive());
        try {
            Post updatedPost = this.postService.updatePost(newPost);
            return new ResponseEntity(updatedPost, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity deletePost(@PathVariable("postId") String postId, HttpServletRequest request) {
        Optional<Post> post = this.postService.findPostById(postId);
        if(!post.isPresent()) {
            return new ResponseEntity("The post was not found", HttpStatus.NOT_FOUND);
        }
        if(request.isUserInRole("BLOGGER") && !isAuthorTheCurrentUser(post, request)) {
            return new ResponseEntity("You have no permission to delete other user's posts.", HttpStatus.FORBIDDEN);
        }
        try {
            Post deletedPost = this.postService.deletePost(postId);
            return new ResponseEntity(deletedPost, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isAuthorTheCurrentUser(Optional<Post> post, HttpServletRequest request) {
        User currentUser = this.userService.findUserByEmail(request.getUserPrincipal().getName());
        return post.get().getAuthor().equals(currentUser.getEmail());
    }
}
