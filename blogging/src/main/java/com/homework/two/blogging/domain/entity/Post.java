package com.homework.two.blogging.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Document("posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    @Id
    private String id;

    private String title;

    private String author;

    private String text;

    private Set<String> tags;

    private String url;

    private LocalDateTime created = LocalDateTime.now();

    private LocalDateTime modified = LocalDateTime.now();

    private boolean active;
}
