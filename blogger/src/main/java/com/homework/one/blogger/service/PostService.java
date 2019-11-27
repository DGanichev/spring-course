package com.homework.one.blogger.service;

import com.homework.one.blogger.domain.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {

    List<Post> getAllPosts();

    Post savePost(Post post);

    Post updatePost(Post post) throws Exception;

    Post deletePost(String postId) throws Exception;

    Optional<Post> findPostById(String postId);
}
