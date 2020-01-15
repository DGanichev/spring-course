package com.homework.two.blogging.web;

import com.homework.two.blogging.domain.entity.Post;
import com.homework.two.blogging.domain.entity.User;
import com.homework.two.blogging.domain.model.binding.PostCreateBindingModel;
import com.homework.two.blogging.service.PostService;
import com.homework.two.blogging.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/posts")
public class PostController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    private static String UPLOADED_FOLDER = "C:/Users/User/IdeaProjects/blogging/src/main/resources/static/images/";

    @GetMapping("/")
    public ModelAndView getPosts(Authentication authentication, ModelAndView modelAndView) {
        List<Post> posts = this.postService.getAllPosts();
        modelAndView.addObject("posts", posts);

        if (this.getPrincipalAuthority(authentication) != null && this.getPrincipalAuthority(authentication).equals("ROLE_ADMIN")) {
            return this.view("admin-home", modelAndView);
        }
        return this.view("blogger-home", modelAndView);
    }

    @GetMapping("/{postId}")
    public ModelAndView getPost(@PathVariable("postId") String postId, ModelAndView modelAndView) throws Exception {
        Optional<Post> post = this.postService.findPostById(postId);
        if(!post.isPresent()) {
            throw new Exception("The post was not found!");
        }
        Path path = Paths.get(UPLOADED_FOLDER + "/" + post.get().getAuthor() + "/" + post.get().getId());
        if(Files.exists(path)) {
            List<String> list = Stream.of(new File(path.toString()).listFiles())
                    .filter(file -> !file.isDirectory())
                    .map(File::getName)
                    .collect(Collectors.toList());
            modelAndView.addObject("images", list);
        }
        modelAndView.addObject("post", post.get());
        return this.view("post-details", modelAndView);
    }

    @GetMapping("/create")
    public ModelAndView createPost(ModelAndView modelAndView) {
        PostCreateBindingModel post = new PostCreateBindingModel();
        modelAndView.addObject("post", post);
        return this.view("post-create", modelAndView);
    }

    @PostMapping("/create")
    public ModelAndView createPost(@Valid @ModelAttribute PostCreateBindingModel posReq, @RequestParam("file") MultipartFile file, BindingResult bindingResult) throws Exception {
        if(bindingResult.hasErrors()) {
            return this.view("post-create");
        }
        Post p = this.modelMapper.map(posReq, Post.class);
        p.setAuthor(SecurityContextHolder.getContext().getAuthentication().getName());
        Post created = this.postService.savePost(p);
        if(!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                Path destinationPath = Paths.get(UPLOADED_FOLDER + '/' + created.getAuthor());
                if(!Files.exists(destinationPath)) {
                    Files.createDirectory(destinationPath);
                }
                Path destinationPath_2 = Paths.get(UPLOADED_FOLDER + '/' + created.getAuthor() + "/" + created.getId());
                if(!Files.exists(destinationPath_2)) {
                    Files.createDirectory(destinationPath_2);
                }
                Path path = Paths.get(UPLOADED_FOLDER + "/" + created.getAuthor() + "/" + created.getId() + "/"+ file.getOriginalFilename());
                Files.write(path, bytes);
            } catch (IOException e) {
                throw new Exception(e);
            }
        }
        return this.redirect("/home");
    }

    @GetMapping("/edit/{postId}")
    public ModelAndView editPost(@PathVariable("postId") String postId, ModelAndView modelAndView) throws Exception {
        Optional<Post> post = this.postService.findPostById(postId);
        if(!post.isPresent()) {
            throw new Exception("The post was not found!");
        }
        modelAndView.addObject("post", post.get());
        return this.view("post-edit", modelAndView);
    }

    @PostMapping("/edit/{postId}")
    public ModelAndView editPost(@PathVariable("postId") String postId, @Valid @ModelAttribute PostCreateBindingModel postReq, @RequestParam("file") MultipartFile file,  HttpServletRequest request, BindingResult bindingResult) throws Exception {
        if(bindingResult.hasErrors()) {
            return this.view("post-edit");
        }
        if(!postId.equals(postReq.getPostId())) {
            throw new Exception("The ids does not match.");
        }
        Optional<Post> post = this.postService.findPostById(postId);
        if(!post.isPresent()) {
            throw new Exception("The post was not found");
        }
        if(request.isUserInRole("BLOGGER") && !isAuthorTheCurrentUser(post, request)) {
            throw new Exception("You have no permission to edit other user's posts.");
        }
        Post newPost = modelMapper.map(postReq, Post.class);
        newPost.setId(post.get().getId());
        newPost.setAuthor(post.get().getAuthor());
        newPost.setModified(LocalDateTime.now());
        newPost.setCreated(post.get().getCreated());
        try {
            this.postService.updatePost(newPost);
            if(!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    Path destinationPath = Paths.get(UPLOADED_FOLDER + '/' + post.get().getAuthor());
                    if(!Files.exists(destinationPath)) {
                        Files.createDirectory(destinationPath);
                    }
                    Path destinationPath_2 = Paths.get(UPLOADED_FOLDER + '/' + post.get().getAuthor() + "/" + post.get().getId());
                    if(!Files.exists(destinationPath_2)) {
                        Files.createDirectory(destinationPath_2);
                    }
                    Path path = Paths.get(UPLOADED_FOLDER + "/" + post.get().getAuthor() + "/" + post.get().getId() + "/"+ file.getOriginalFilename());
                    Files.write(path, bytes);
                } catch (IOException e) {
                    throw new Exception(e);
                }
            }
            return this.redirect("/home");
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    @GetMapping("/delete/{postId}")
    public ModelAndView deletePost(@PathVariable("postId") String postId, HttpServletRequest request) throws Exception {
        Optional<Post> post = this.postService.findPostById(postId);
        if(!post.isPresent()) {
            throw new Exception("The post was not found");
        }
        if(request.isUserInRole("BLOGGER") && !isAuthorTheCurrentUser(post, request)) {
            throw new Exception("You have no permission to delete other user's posts.");
        }
        try {
            this.postService.deletePost(postId);
            Path path = Paths.get(UPLOADED_FOLDER + "/" + post.get().getAuthor() + "/" + post.get().getId());
            if(Files.exists(path)) {
                this.deleteDirectory(path.toFile());
            }
             return this.redirect("/home");
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private boolean isAuthorTheCurrentUser(Optional<Post> post, HttpServletRequest request) {
        User currentUser = this.userService.findUserByEmail(request.getUserPrincipal().getName());
        return post.get().getAuthor().equals(currentUser.getEmail());
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
