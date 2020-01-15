package com.homework.two.blogging.domain.model.binding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateBindingModel {
    private String postId;

    private String title;

    private String text;

    private Set<String> tags;

    private String url;

    private boolean active;
}
