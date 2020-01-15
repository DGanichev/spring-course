package com.homework.two.blogging.service.impl;

import com.homework.two.blogging.domain.entity.Role;
import com.homework.two.blogging.domain.entity.User;
import com.homework.two.blogging.repository.RoleRepository;
import com.homework.two.blogging.repository.UserRepository;
import com.homework.two.blogging.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public User findUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findUserById(String id) {
        return this.userRepository.findById(id);
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Role userRole = this.roleRepository.findByRole("ROLE_BLOGGER");
        user.setActive(true);
        user.setRoles(new HashSet<>(Arrays.asList(userRole)));
        userRepository.save(user);
        return user;
    }

    @Override
    public User updateUser(User user) throws UsernameNotFoundException {
        Optional<User> oldUser = this.userRepository.findById(user.getId());
        if(!oldUser.isPresent()) {
            throw new UsernameNotFoundException("The user was not found!");
        }
        user.setEmail(oldUser.get().getEmail());
        user.setPassword(oldUser.get().getPassword());
        user.setRoles(oldUser.get().getRoles());
        user.setCreated(oldUser.get().getCreated());
        user.setModified(LocalDateTime.now());
        return this.userRepository.save(user);
    }

    @Override
    public User deleteUser(String userId) throws UsernameNotFoundException {
        Optional<User> oldUser = this.userRepository.findById(userId);
        if(!oldUser.isPresent()) {
            throw new UsernameNotFoundException("The user was not found!");
        }
        this.userRepository.deleteById(userId);
        return oldUser.get();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email);
        if(user != null) {
            List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());
            return buildUserForAuthentication(user, authorities);
        } else {
            throw new UsernameNotFoundException("Username not found.");
        }
    }

    private List<GrantedAuthority> getUserAuthority(Set<Role> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        userRoles.forEach((role) -> {
            roles.add(new SimpleGrantedAuthority(role.getRole()));
        });

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
        return grantedAuthorities;
    }

    private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }
}
