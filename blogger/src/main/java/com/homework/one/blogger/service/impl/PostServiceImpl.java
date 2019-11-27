package com.homework.one.blogger.service.impl;

import com.homework.one.blogger.domain.entity.Post;
import com.homework.one.blogger.repository.PostRepository;
import com.homework.one.blogger.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Override
    public List<Post> getAllPosts() {
        return this.postRepository.findAll();
    }

    @Override
    public Post savePost(Post post) {
        return this.postRepository.save(post);
    }

    @Override
    public Post updatePost(Post post) throws Exception {
        Optional<Post> oldPost = this.postRepository.findById(post.getId());
        if(!oldPost.isPresent()) {
            throw new Exception("The post was not found!");
        }
        post.setAuthor(oldPost.get().getAuthor());
        post.setCreated(oldPost.get().getCreated());
        post.setModified(LocalDateTime.now());
        return this.postRepository.save(post);
    }

    @Override
    public Post deletePost(String postId) throws Exception {
        Optional<Post> oldPost = this.postRepository.findById(postId);
        if(!oldPost.isPresent()) {
            throw new Exception("The post was not found!");
        }
        this.postRepository.deleteById(postId);
        return oldPost.get();
    }

    @Override
    public Optional<Post> findPostById(String postId) {
        return this.postRepository.findById(postId);
    }
}
