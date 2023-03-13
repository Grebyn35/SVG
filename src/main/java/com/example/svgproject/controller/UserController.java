package com.example.svgproject.controller;

import com.example.svgproject.repository.UserRepository;
import com.example.svgproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/") public String adminStart(){
        return "admin-start";
    }

}
