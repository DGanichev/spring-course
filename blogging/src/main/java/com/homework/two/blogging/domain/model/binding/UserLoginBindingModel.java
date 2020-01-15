package com.homework.two.blogging.domain.model.binding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginBindingModel {

    private String username;

    private String password;
}
