package com.homework.two.blogging.service;

import com.homework.two.blogging.domain.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {

    List<Post> getAllPosts();

    List<Post> getFirst15PostsSortedByCreateDate();

    Post savePost(Post post);

    Post updatePost(Post post) throws Exception;

    Post deletePost(String postId) throws Exception;

    Optional<Post> findPostById(String postId);
}
