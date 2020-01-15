package com.homework.two.blogging.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/upload")
public class UploadController extends BaseController {

    private static String UPLOADED_FOLDER = "C:\\Users\\User\\IdeaProjects\\blogging\\temp";

    @GetMapping("/")
    public ModelAndView index() {
        return this.view("upload");
    }

    @PostMapping("/upload")
    public ModelAndView singleFileUpload(@RequestParam("file") MultipartFile file,
                                  RedirectAttributes redirectAttributes) throws Exception {
        if (file.isEmpty()) {
            return this.redirect("/");
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");

        } catch (IOException e) {
            throw new Exception(e);
        }
        return this.redirect("/");
    }

}
