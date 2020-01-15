package com.homework.two.blogging.domain.model.binding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEditBindingModel {
    private String userId;

    private String firstName;

    private String lastName;

    private String url;

    private boolean active;
}
