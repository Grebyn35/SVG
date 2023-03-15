package com.example.svgproject.controller;

import com.example.svgproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AdminController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/admin/annonser") public String adminPosts(){
        return "admin-annonser";
    }
    @GetMapping("/admin/ny-annons") public String adminNewPost(){
        return "admin-ny-annons";
    }
    @GetMapping("/admin/ny-nyhet") public String adminNewNews(){
        return "admin-ny-nyhet";
    }
    @GetMapping("/admin/ny-vardgivare") public String adminNewUser(){
        return "admin-ny-vardgivare";
    }
    @GetMapping("/admin/nyheter") public String adminNews(){
        return "admin-ny-vardgivare";
    }
    @GetMapping("/admin/start") public String adminStart(){
        return "admin-start";
    }

    @GetMapping("/admin/redigera-annons/{id}") public String adminEditPost(@PathVariable long id){
        return "admin-redigera-annons";
    }
    @GetMapping("/admin/redigera-nyhet/{id}") public String adminEditNews(@PathVariable long id){
        return "admin-redigera-nyhet";
    }
    @GetMapping("admin/redigera-vardgivare/{id}") public String adminStart(@PathVariable long id){
        return "admin-redigera-vardgivare";
    }
}
