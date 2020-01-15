package com.homework.two.blogging.web;

import com.homework.two.blogging.domain.entity.User;
import com.homework.two.blogging.domain.model.binding.UserRegisterBindingModel;
import com.homework.two.blogging.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class AuthController extends BaseController {
    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/login")
    public ModelAndView login() {
        return this.view("login");
    }

    @GetMapping("/register")
    public ModelAndView register() {
        return this.view("register");
    }

    @PostMapping("/register")
    public ModelAndView registerConfirm(@Valid @ModelAttribute UserRegisterBindingModel userRegisterBindingModel, BindingResult bindingResult) {
        if(!userRegisterBindingModel.getPassword().equals(userRegisterBindingModel.getConfirmPassword())) {
            return this.view("register");
        }
        User userExists = userService.findUserByEmail(userRegisterBindingModel.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.userRegisterBindingModel",
                            "The email has already exist.");
        }
        if(bindingResult.hasErrors()) {
            return this.view("register");
        }

        this.userService.saveUser(this.modelMapper.map(userRegisterBindingModel, User.class));
        return this.redirect("/login");
    }
}
