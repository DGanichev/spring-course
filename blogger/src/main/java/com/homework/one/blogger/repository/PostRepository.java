package com.homework.one.blogger.repository;

import com.homework.one.blogger.domain.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {
}
