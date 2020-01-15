package com.homework.two.blogging.web;

import com.homework.two.blogging.domain.entity.User;
import com.homework.two.blogging.domain.model.binding.UserCreateBindingModel;
import com.homework.two.blogging.domain.model.binding.UserEditBindingModel;
import com.homework.two.blogging.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/users")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    private static String UPLOADED_FOLDER = "C:/Users/User/IdeaProjects/blogging/src/main/resources/static/images/";

    @GetMapping("/")
    public ModelAndView getUsers(ModelAndView modelAndView) {
        List<User> users = this.userService.getAllUsers();
        modelAndView.addObject("users", users);
        return this.view("users-list", modelAndView);
    }

    @GetMapping("/{email}")
    public ModelAndView getUser(@PathVariable("email") String email, ModelAndView modelAndView, HttpServletRequest request) throws Exception {
        User user = this.userService.findUserByEmail(email);
        if(user == null) {
            throw new Exception("The user was not found!");
        }
        if(request.isUserInRole("BLOGGER") && !request.getUserPrincipal().getName().equals(user.getEmail())) {
            throw new Exception("You have no permission to get details about other user.");
        }
        Path path = Paths.get(UPLOADED_FOLDER + "/" + user.getEmail() + "/profile/");
        if(Files.exists(path)) {
            List<String> list = Stream.of(new File(path.toString()).listFiles())
                    .filter(file -> !file.isDirectory())
                    .map(File::getName)
                    .collect(Collectors.toList());
            modelAndView.addObject("images", list);
        }
        modelAndView.addObject("user", user);
        return this.view("user-details", modelAndView);
    }

    @GetMapping("/create")
    public ModelAndView createUser(ModelAndView modelAndView) {
        User user = new User();
        modelAndView.addObject("user", user);
        return this.redirect("/users");
    }

    @PostMapping("/create")
    public ModelAndView createUser(@RequestBody UserCreateBindingModel userReq, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return this.view("");
        }
        User user = new User();
        user.setEmail(userReq.getEmail());
        user.setFirstName(userReq.getFirstName());
        user.setLastName(userReq.getLastName());
        user.setUrl(userReq.getUrl());
        user.setPassword(userReq.getPassword());
        this.userService.saveUser(user);
        return this.redirect("/users");
    }

    @GetMapping("/edit/{userId}")
    public ModelAndView editPost(@PathVariable("userId") String userId, ModelAndView modelAndView) throws Exception {
        Optional<User> user = this.userService.findUserById(userId);
        if(!user.isPresent()) {
            throw new Exception("The user was not found!");
        }
        modelAndView.addObject("user", user.get());
        return this.view("user-edit", modelAndView);
    }

    @PostMapping("/edit/{userId}")
    public ModelAndView editUser(@PathVariable("userId") String userId, @Valid @ModelAttribute UserEditBindingModel userReq, @RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        if(!userId.equals(userReq.getUserId())) {
            throw new Exception("The ids does not match.");
        }
        Optional<User> currentUser = this.userService.findUserById(userId);
        User user = modelMapper.map(userReq, User.class);
        user.setId(currentUser.get().getId());
        try {
            this.userService.updateUser(user);
            if(!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    Path destinationPath = Paths.get(UPLOADED_FOLDER + '/' + currentUser.get().getEmail());
                    if(!Files.exists(destinationPath)) {
                        Files.createDirectory(destinationPath);
                    }
                    Path destinationPath_2 = Paths.get(UPLOADED_FOLDER + '/' + currentUser.get().getEmail() + "/profile");
                    if(!Files.exists(destinationPath_2)) {
                        Files.createDirectory(destinationPath_2);
                    }
                    Path path = Paths.get(UPLOADED_FOLDER + "/" + currentUser.get().getEmail() + "/profile/"+ file.getOriginalFilename());
                    Files.write(path, bytes);
                } catch (IOException e) {
                    throw new Exception(e);
                }
            }
            return this.redirect("/home");
        } catch (UsernameNotFoundException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    @GetMapping("/delete/{userId}")
    public ModelAndView deleteUser(@PathVariable("userId") String userId) throws Exception {
        Optional<User> user = this.userService.findUserById(userId);
        if(!user.isPresent()) {
            throw new Exception("The user was not found!");
        }
        user.get().setActive(false);
        this.userService.updateUser(user.get());
        Path path = Paths.get(UPLOADED_FOLDER + "/" + user.get().getEmail() + "/profile/");
        System.out.println(path.toString());
        if(Files.exists(path)) {
            this.deleteDirectory(path.toFile());
        }
        return this.redirect("/home");
    }

    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
