package com.homework.two.blogging.service;

import com.homework.two.blogging.domain.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    List<User> getAllUsers();

    User findUserByEmail(String email);

    Optional<User> findUserById(String id);

    User saveUser(User user);

    User updateUser(User user) throws UsernameNotFoundException;

    User deleteUser(String userId) throws UsernameNotFoundException;
}
