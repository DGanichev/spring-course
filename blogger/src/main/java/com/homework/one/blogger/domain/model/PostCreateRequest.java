package com.homework.one.blogger.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {
    private String postId;

    private String title;

    private String text;

    private Set<String> tags;

    private String url;

    private boolean active;
}
