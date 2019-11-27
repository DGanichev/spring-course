package com.homework.one.blogger.web;

import com.homework.one.blogger.domain.entity.User;
import com.homework.one.blogger.domain.model.UserCreateRequest;
import com.homework.one.blogger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    public ResponseEntity getUsers() {
        List<User> users = this.userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity getUser(@PathVariable("userId") String userId, HttpServletRequest request) {
        Optional<User> user = this.userService.findUserById(userId);
        if(!user.isPresent()) {
            return new ResponseEntity<>("The user was not found!", HttpStatus.NOT_FOUND);
        }
        if(request.isUserInRole("BLOGGER") && !request.getUserPrincipal().getName().equals(user.get().getEmail())) {
            return new ResponseEntity("You have no permission to get details about other user.", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity createUser(@RequestBody UserCreateRequest userReq) {
        User user = new User();
        user.setEmail(userReq.getEmail());
        user.setFirstName(userReq.getFirstName());
        user.setLastName(userReq.getLastName());
        user.setUrl(userReq.getUrl());
        user.setPassword(userReq.getPassword());
        this.userService.saveUser(user);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.
                        fromCurrentRequest().
                        pathSegment("{id}").
                        build(user.getId()))
                .body(user);
    }

    @PutMapping("/{userId}")
    public ResponseEntity editUser(@PathVariable("userId") String userId, @RequestBody UserCreateRequest userReq, HttpServletRequest request) {
        if(!userId.equals(userReq.getUserId())) {
            return new ResponseEntity("The ids does not match.", HttpStatus.BAD_REQUEST);
        }
        User currentUser = this.userService.findUserByEmail(request.getUserPrincipal().getName());
        if(request.isUserInRole("BLOGGER") && !userReq.getEmail().equals(currentUser.getEmail())) {
            return new ResponseEntity("You have no permission to edit other user's details.", HttpStatus.FORBIDDEN);
        }
        User user = new User();
        user.setId(userReq.getUserId());
        user.setEmail(userReq.getEmail());
        user.setFirstName(userReq.getFirstName());
        user.setLastName(userReq.getLastName());
        user.setUrl(userReq.getUrl());
        user.setPassword(userReq.getPassword());
        try {
            User updatedUser = this.userService.updateUser(user);
            return new ResponseEntity(updatedUser, HttpStatus.OK);
        } catch (UsernameNotFoundException ex) {
            return new ResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUser(@PathVariable("userId") String userId) {
        try {
            User deletedUser = this.userService.deleteUser(userId);
            return new ResponseEntity(deletedUser, HttpStatus.OK);
        } catch (UsernameNotFoundException ex) {
            return new ResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
