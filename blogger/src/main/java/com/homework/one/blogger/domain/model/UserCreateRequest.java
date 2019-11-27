package com.homework.one.blogger.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest implements Serializable {
    private String userId;

    private String email;

    private String firstName;

    private String lastName;

    private String password;

    private String url;
}
