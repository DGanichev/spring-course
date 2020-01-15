package com.homework.two.blogging.repository;

import com.homework.two.blogging.domain.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findTop15ByOrderByCreatedDesc();
}
