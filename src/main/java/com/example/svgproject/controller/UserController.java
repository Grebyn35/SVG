package com.example.svgproject.controller;

import com.example.svgproject.model.Provider;
import com.example.svgproject.model.User;
import com.example.svgproject.repository.ProviderRepository;
import com.example.svgproject.repository.UserRepository;
import com.example.svgproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@Controller
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    private UserService servDao;

    @GetMapping("/") public String home(){
        return "hem";
    }
    @GetMapping("/kontakt") public String contact(){
        return "kontakt";
    }
    @GetMapping("/aterstall-losenord") public String resetPassword(){
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
    @GetMapping("/skapa-konto") public String createAccount(Model model){
        model.addAttribute("userObject", new User());
        return "skapa-konto";
    }
    @PostMapping("/skapa-konto")
    public String createAccountPost(HttpServletRequest request, @Valid @ModelAttribute("userObject") User user){
        user.setPassword(servDao.enCryptedPassword(user));
        if(userRepository.findByEmail(request.getParameter("email"))==null){
            userRepository.save(user);
        }
        return "redirect:/login";
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
    @GetMapping("/vardgivare") public String userPage(Model model, @RequestParam("page") int page){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Provider> providers = providerRepository.findAll(pageable);
        model.addAttribute("providers", providers.getContent());
        model.addAttribute("totalHits", providers.getTotalPages());
        model.addAttribute("page", page);
        return "vardgivare";
    }
    @GetMapping("/search_vardgivare")
    public String updateArticles(Model model, HttpServletRequest request, @RequestParam("search_input") String searchInput, @RequestParam("branch_type") String branchType, @RequestParam("grade") String grade, @RequestParam("page") int page, @RequestParam("county") String county){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Provider> providers = providerRepository.findAllByNameContainingAndTypeListContainingAndCountyContainingAndGradeContaining(searchInput, branchType, county, grade, pageable);
        System.out.println(providers.getContent());
        model.addAttribute("providers", providers.getContent());
        model.addAttribute("totalHits", providers.getTotalPages());
        model.addAttribute("page", page);
        return "vardgivare :: .tableSearch";
    }
    @GetMapping("/vardgivare/{id}") public String userPageNew(@PathVariable long id){
        return "vardgivare-template";
    }

}
