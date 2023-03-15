package com.example.svgproject.controller;

import com.example.svgproject.repository.UserRepository;
import com.example.svgproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class UserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/") public String home(){
        return "hem";
    }
    @GetMapping("/kontakt") public String contact(){
        return "kontakt";
    }
    @GetMapping("/reset-password") public String resetPassword(){
        return "aterstall-losenord";
    }
    @GetMapping("/kvalitet") public String qualityPage(){
        return "kvalitet";
    }
    @GetMapping("/lista-foretag") public String listCompanyPage(){
        return "lista-foretag";
    }
    @GetMapping("/login") public String loginPage(){
        return "logga-in";
    }
    //Denna funkar inte?
    @GetMapping("/nyheter/{id}") public String newsTemplatePage(@PathVariable long id){
        return "nyheter-template";
    }
    @GetMapping("/nyheter") public String newsPage(){
        return "nyheter";
    }
    @GetMapping("/om-oss") public String aboutUsPage(){
        return "om-oss";
    }

    @GetMapping("/anvandarvillkor") public String anvandarVillkor(){
        return "om-oss";
    }
    @GetMapping("/integritetspolicy") public String integrityPolicy(){
        return "om-oss";
    }
    @GetMapping("/cookies") public String cookies(){
        return "om-oss";
    }
    @GetMapping("/vardgivare") public String userPage(){
        return "vardgivare";
    }
    @GetMapping("/vardgivare/{id}") public String userPageNew(@PathVariable long id){
        return "vardgivare-template";
    }

}
