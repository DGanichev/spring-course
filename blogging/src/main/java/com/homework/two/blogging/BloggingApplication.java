package com.homework.two.blogging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BloggingApplication {
    public static void main(String[] args) {
        System.setProperty("server.servlet.context-path", "/");
        SpringApplication.run(BloggingApplication.class, args);
    }

}
