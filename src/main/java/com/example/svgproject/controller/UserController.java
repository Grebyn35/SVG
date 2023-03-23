package com.example.svgproject.controller;

import com.example.svgproject.model.NewsLetter;
import com.example.svgproject.model.Nyhet;
import com.example.svgproject.model.Provider;
import com.example.svgproject.model.User;
import com.example.svgproject.repository.NewsLetterRepository;
import com.example.svgproject.repository.NyhetRepository;
import com.example.svgproject.repository.ProviderRepository;
import com.example.svgproject.repository.UserRepository;
import com.example.svgproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


@Controller
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    private UserService servDao;

    @Autowired
    NyhetRepository nyhetRepository;

    @Autowired
    NewsLetterRepository newsLetterRepository;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/") public String home(Model model){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Nyhet> nyheter = nyhetRepository.findAllByCategoryContaining("", pageable);
        model.addAttribute("nyheter", nyheter.getContent());
        model.addAttribute("totalHits", nyheter.getTotalPages());
        model.addAttribute("page", 0);
        return "hem";
    }
    @GetMapping("/kontakt") public String contact(){
        return "kontakt";
    }
    @PostMapping("/kontakt") public String contactPost(HttpServletRequest request, RedirectAttributes redirectAttributes){
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String tel = request.getParameter("tel");
        String content = request.getParameter("content");
        sendEmail(content, email, name, tel);
        sendConfirmationEmail(email, name);
        redirectAttributes.addFlashAttribute("notificationMsg", "contact");
        return "redirect:/";
    }
    @PostMapping("/nyhetsbrev") public String newsLetterPost(HttpServletRequest request, RedirectAttributes redirectAttributes){
        String email = request.getParameter("email");
        NewsLetter newsLetter = new NewsLetter();
        newsLetter.setEmail(email);
        newsLetter.setRegistered(returnDateWithTime());
        newsLetterRepository.save(newsLetter);
        redirectAttributes.addFlashAttribute("notificationMsg", "newsLetter");
        return "redirect:/";
    }
    @GetMapping("/optout/{email}") public String optOutNewsLetter(HttpServletRequest request, RedirectAttributes redirectAttributes, @PathVariable String email){
        NewsLetter newsLetter = newsLetterRepository.findByEmail(email);
        newsLetterRepository.delete(newsLetter);
        redirectAttributes.addFlashAttribute("notificationMsg", "newsLetterRemoved");
        return "redirect:/";
    }
    public String returnDateWithTime(){
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));
        return df.format(date);
    }
    @PostMapping("/lista-foretag") public String listCmpPost(HttpServletRequest request, RedirectAttributes redirectAttributes){
        String listCmpName = request.getParameter("listCmpName");
        String listContactName = request.getParameter("listContactName");
        String listTel = request.getParameter("listTel");
        String listEmail = request.getParameter("listEmail");
        sendListingEmail(listCmpName, listContactName, listTel, listEmail);
        sendListingConfirmationEmail(listEmail, listContactName);
        redirectAttributes.addFlashAttribute("notificationMsg", "listing");
        return "redirect:/";
    }
    public void sendListingEmail(String cmpName, String listContactName, String listTel, String listEmail){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(listEmail, listContactName);
            helper.setTo("elliot@ensotech.io");
            helper.setSubject("Ett företag har skickat in en ny anmälan");
            helper.setText("Ett företag har fyllt i formuläret för att lista sig hos er. Nedan finner ni samtlig information från deras anmälan"
                    +"<br>företagsnamn: " + cmpName
                    +"<br>kontakt namn: " + listContactName
                    +"<br>tel: " + listTel
                    +"<br>epost: " + listEmail, true);
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendListingConfirmationEmail(String from, String name){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("noreply@sverigesvard.se", "Sveriges Vårdgivare");
            helper.setTo(from);
            helper.setSubject("Vi har tagit emot din förfrågan!");
            helper.setText("Hej " + name + "! Vi har tagit emot din förfrågan om att lista ditt företag och återkommer så snart vi kan.", true);
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendEmail(String text, String from, String name, String tel){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from, name);
            helper.setTo("elliot@ensotech.io");
            helper.setSubject("Nytt kontaktformulär inskickat");
            helper.setText(text + "<br>kundens telefonummer: " + tel, true);
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendConfirmationEmail(String from, String name){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("noreply@sverigesvard.se", "Sveriges Vårdgivare");
            helper.setTo(from);
            helper.setSubject("Vi har tagit emot din kontaktförfrågan!");
            helper.setText("Hej " + name + "! Vi har tagit emot din kontaktförfrågan och återkommer så snart vi kan.", true);
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
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
    @GetMapping("/nyheter/{id}") public String newsTemplatePage(@PathVariable long id, Model model){
        Nyhet nyhet = nyhetRepository.findById(id);
        Pageable pageable = PageRequest.of(0, 3);
        Page<Nyhet> nyheter = nyhetRepository.findAllByCategoryContainingAndIdNot(nyhet.getCategory(), id, pageable);
        model.addAttribute("nyheter", nyheter.getContent());
        model.addAttribute("nyhet", nyhet);
        return "nyheter-template";
    }
    @GetMapping("/nyheter") public String newsPage(@RequestParam("page") int page, @RequestParam("category") String category, Model model){
        Pageable pageable = PageRequest.of(page, 10);
        if(category.contentEquals("all")){
            category = "";
        }
        Page<Nyhet> nyheter = nyhetRepository.findAllByCategoryContaining(category, pageable);
        model.addAttribute("nyheter", nyheter.getContent());
        model.addAttribute("totalHits", nyheter.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("category", category);
        return "nyheter";
    }
    @GetMapping("/search_news")
    public String updateArticlesNews(Model model, HttpServletRequest request, @RequestParam("search_input") String searchInput, @RequestParam("page") int page, @RequestParam("category") String category){
        Pageable pageable = PageRequest.of(page, 10);
        if(category.contentEquals("all")){
            category = "";
        }
        Page<Nyhet> nyheter = nyhetRepository.findAllByTitleContainingAndCategoryContaining(searchInput, category, pageable);
        model.addAttribute("nyheter", nyheter.getContent());
        model.addAttribute("totalHits", nyheter.getTotalPages());
        model.addAttribute("page", page);
        return "nyheter :: .tableSearch";
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

        model.addAttribute("searchInput", "");
        model.addAttribute("branchType", "");
        model.addAttribute("county", "");
        model.addAttribute("grade", "");
        return "vardgivare";
    }
    @GetMapping("/search_vardgivare")
    public String updateArticles(Model model, HttpServletRequest request, @RequestParam("search_input") String searchInput, @RequestParam("branch_type") String branchType, @RequestParam("grade") String grade, @RequestParam("page") int page, @RequestParam("county") String county){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Provider> providers = providerRepository.findAllByNameContainingAndTypeListContainingAndCountyContainingAndGradeContaining(searchInput, branchType, county, grade, pageable);
        model.addAttribute("providers", providers.getContent());
        model.addAttribute("totalHits", providers.getTotalPages());
        model.addAttribute("page", page);
        return "vardgivare :: .tableSearch";
    }
    @PostMapping("/vardgivare_search")
    public String searchProviderCustom(Model model, HttpServletRequest request){
        Pageable pageable = PageRequest.of(0, 10);
        String searchInput = request.getParameter("search_input");
        String branchType = request.getParameter("branchType");
        String county = request.getParameter("county");
        String grade = request.getParameter("grade");

        model.addAttribute("searchInput", searchInput);
        model.addAttribute("branchType", branchType);
        model.addAttribute("county", county);
        model.addAttribute("grade", grade);
        Page<Provider> providers = providerRepository.findAllByNameContainingAndTypeListContainingAndCountyContainingAndGradeContaining(searchInput, branchType, county, grade, pageable);
        model.addAttribute("providers", providers.getContent());
        model.addAttribute("totalHits", providers.getTotalPages());
        model.addAttribute("page", 0);
        return "vardgivare";
    }
    @GetMapping("/vardgivare/{id}") public String userPageNew(@PathVariable long id, Model model){
        Provider provider = providerRepository.findById(id);
        model.addAttribute("provider", provider);
        return "vardgivare-template";
    }
    @GetMapping("vardgivare_search") public String vardgivareRedirect(){
        return "redirect:/vardgivare?page=0";
    }

}
