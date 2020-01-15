package com.homework.two.blogging.web;

import com.homework.two.blogging.domain.entity.Post;
import com.homework.two.blogging.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class HomeController extends BaseController {

    @Autowired
    private PostService postService;

    @GetMapping("/")
    public ModelAndView index(ModelAndView modelAndView) {
        List<Post> list = postService.getFirst15PostsSortedByCreateDate();
        modelAndView.addObject("posts", list);
        return this.view("index", modelAndView);
    }

    @GetMapping("/home")
    public ModelAndView home(Authentication authentication, ModelAndView modelAndView) {
        modelAndView.addObject("posts", this.postService.getAllPosts());

        if(this.getPrincipalAuthority(authentication) != null && this.getPrincipalAuthority(authentication).equals("ROLE_ADMIN")){
            return this.view("admin-home", modelAndView);
        }
        return this.view("blogger-home", modelAndView);
    }
}
