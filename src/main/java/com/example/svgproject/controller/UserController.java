package com.example.svgproject.controller;

import com.example.svgproject.model.*;
import com.example.svgproject.repository.*;
import com.example.svgproject.service.UserService;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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
import javax.validation.Valid;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


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
    PostRepository postRepository;

    @Autowired
    UserDocsRepository userDocsRepository;
    @Autowired
    UserQualityTalesRepository userQualityTalesRepository;
    @Autowired
    UserRegistryRepository userRegistryRepository;
    @Autowired
    UserReportsRepository userReportsRepository;

    @Autowired
    NewsLetterRepository newsLetterRepository;

    @Autowired
    SlideshowDocsRepository slideshowDocsRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    CoverImageRepository coverImageRepository;

    static String recipentEmail = "elliot@ensotech.io";

    @GetMapping("/") public String home(Model model){
        Pageable pageable = PageRequest.of(0, 5);
        Pageable pageablePosts = PageRequest.of(0, 20);

        CoverImage coverImage = coverImageRepository.findByPageName("startsida");
        model.addAttribute("coverImage", coverImage);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -3);
        java.sql.Date timeInterval = new java.sql.Date(cal.getTimeInMillis());
        Page<Nyhet> nyheter = nyhetRepository.findAllByDateCreatedAfterOrderByPublishedDesc(timeInterval, pageable);

        model.addAttribute("nyheter", nyheter.getContent());
        model.addAttribute("totalHits", nyheter.getTotalPages());
        model.addAttribute("page", 0);
        Page<Post> posts = postRepository.findAllByStatusTrueAndPageOrderByPublishedDesc("Startsida", pageablePosts);
        ArrayList<Provider> promotedProviders = providerRepository.findAllByOtherSettingsContaining("Utvald vårdgivare");
        model.addAttribute("posts", posts);
        model.addAttribute("promotedProviders", promotedProviders);
        return "hem";
    }
    @GetMapping("/kontakt") public String contact(Model model){
        CoverImage coverImage = coverImageRepository.findByPageName("kontakt");
        model.addAttribute("coverImage", coverImage);
        return "kontakt";
    }
    @GetMapping("/updateCountys12345") public String testaaa(Model model){
        /*String csvFile = "C:\\Users\\Grebyn\\IdeaProjects\\SVG\\src\\main\\resources\\static\\providers.csv";

        try (FileReader fileReader = new FileReader(csvFile)) {
            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(1) // Skip the header line
                    .build();


            List<String[]> records = csvReader.readAll();

            for(int i = 0; i<records.size();i++){
                System.out.println(i + "/" + records.size());
                ArrayList<Provider> providers = providerRepository.findAllByOrgNr(records.get(i)[1]);
                for(int c = 0; c<providers.size();c++){
                    providers.get(c).setCounty(records.get(i)[2]);
                }
                providerRepository.saveAll(providers);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return "redirect:/";
    }

    @PostMapping("/kontakt") public String contactPost(HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
        String token = request.getParameter("g-token");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String tel = request.getParameter("tel");
        String content = request.getParameter("content");
        if(captchaValidation(token)) {
            sendContactFormEmail(content, email, name, tel);
            sendContactFormConfirmationEmail(email, name);
            redirectAttributes.addFlashAttribute("notificationMsg", "contact");
        }
        else{
            redirectAttributes.addFlashAttribute("notificationMsg", "captchaFailed");
            System.out.println("token invalid for contact form");
        }
        return "redirect:/";
    }
    public boolean captchaValidation(String responseToken) throws IOException {
        HttpPost post = new HttpPost("https://www.google.com/recaptcha/api/siteverify");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("secret", "6Lfe5HghAAAAAK7CRNDwqDMmvwu2-8rzY8AwXJKR"));
        urlParameters.add(new BasicNameValuePair("response", responseToken));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            String responseString = EntityUtils.toString(response.getEntity());
            String succesString = responseString.split("\"success\": ")[1];
            System.out.println(responseString);
            String resultString = succesString.split(",")[0];
            boolean result = Boolean.parseBoolean(resultString);
            if(result) {
                return true;
            }
            return false;
        }
    }
    @PostMapping("/nyhetsbrev") public String newsLetterPost(HttpServletRequest request, RedirectAttributes redirectAttributes){
        String email = request.getParameter("email");
        NewsLetter newsLetter = new NewsLetter();
        newsLetter.setEmail(email);
        newsLetter.setRegistered(returnDateWithTime());
        newsLetterRepository.save(newsLetter);
        sendNewsLetterEmail(email);
        sendNewsLetterEmailToAdmin(email);
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
    @PostMapping("/lista-foretag") public String listCmpPost(HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) throws IOException {
        String token = request.getParameter("g-token");
        String listCmpName = request.getParameter("listCmpName");
        String listContactName = request.getParameter("listContactName");
        String listTel = request.getParameter("listTel");
        String listEmail = request.getParameter("listEmail");

        if(captchaValidation(token)) {
            sendListingEmail(listCmpName, listContactName, listTel, listEmail);
            sendListingConfirmationEmail(listEmail, listContactName);
            redirectAttributes.addFlashAttribute("notificationMsg", "listing");
        }
        else{
            redirectAttributes.addFlashAttribute("notificationMsg", "captchaFailed");
            System.out.println("token invalid for listing company");
        }
        return "redirect:/";
    }
    public void sendListingEmail(String cmpName, String listContactName, String listTel, String listEmail){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(listEmail, listContactName);
            helper.setTo(recipentEmail);
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
    public void sendContactFormEmail(String text, String from, String name, String tel){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from, name);
            helper.setTo(recipentEmail);
            helper.setSubject("Nytt kontaktformulär inskickat");
            helper.setText(text + "<br>kundens telefonummer: " + tel, true);
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendNewsLetterEmail(String from){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("noreply@sverigesvard.se", "Sveriges Vårdgivare");
            helper.setTo(from);
            helper.setSubject("Nytt kontaktformulär inskickat");
            helper.setText("Tack för att du har prenumererat på våra nyhetsbrev!", true);
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendNewsLetterEmailToAdmin(String from){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from, from);
            helper.setTo(recipentEmail);
            helper.setSubject("En person har prenumererat på erat nyhetsbrev");
            helper.setText(from + " har precis prenumererat på erat nyhetsbrev!", true);
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendContactFormConfirmationEmail(String from, String name){
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
    @GetMapping("/kvalitet") public String qualityPage(Model model){
        CoverImage coverImage1 = coverImageRepository.findByPageName("kvalitet bild 1");
        CoverImage coverImage2 = coverImageRepository.findByPageName("kvalitet bild 2");
        model.addAttribute("coverImage1", coverImage1);
        model.addAttribute("coverImage2", coverImage2);
        return "kvalitet";
    }
    @GetMapping("/lista-foretag") public String listCompanyPage(Model model){
        CoverImage coverImage = coverImageRepository.findByPageName("lista-foretag");
        model.addAttribute("coverImage", coverImage);
        return "lista-foretag";
    }
    @GetMapping("/lista-foretag-formular") public String listCompanyFormulaPage(Model model){
        CoverImage coverImage = coverImageRepository.findByPageName("lista-foretag");
        model.addAttribute("coverImage", coverImage);
        return "lista-företag-formulär";
    }
    @GetMapping("/login") public String loginPage(){
        return "logga-in";
    }
    @GetMapping("/skapa-konto") public String createAccount(Model model){
        model.addAttribute("userObject", new User());
        return "skapa-konto";
    }
    @PostMapping("/skapa-konto")
    public String createAccountPost(HttpServletRequest request, @Valid @ModelAttribute("userObject") User user, RedirectAttributes redirectAttributes) throws IOException {
        String token = request.getParameter("g-token");
        if(captchaValidation(token)) {
            user.setPassword(servDao.enCryptedPassword(user));
            if(userRepository.findByEmail(request.getParameter("email"))==null){
                userRepository.save(user);
            }
            return "redirect:/login";
        }
        else{
            redirectAttributes.addFlashAttribute("notificationMsg", "captchaFailed");
            System.out.println("token invalid for creating account");
            return "redirect:/";
        }
    }
    //Denna funkar inte?
    @GetMapping("/nyheter/{id}") public String newsTemplatePage(@PathVariable long id, Model model){
        Nyhet nyhet = nyhetRepository.findById(id);
        Pageable pageable = PageRequest.of(0, 3);
        Pageable pageablePosts = PageRequest.of(0, 20);
        Page<Nyhet> nyheter = nyhetRepository.findAllByIdIsNotNullAndIdNotOrderByPublishedDesc(nyhet.getId(), pageable);
        model.addAttribute("nyheter", nyheter.getContent());
        model.addAttribute("nyhet", nyhet);
        Page<Post> posts = postRepository.findAllByStatusTrueAndPageOrderByPublishedDesc("Nyheter", pageablePosts);
        model.addAttribute("posts", posts);
        return "nyheter-template";
    }
    @GetMapping("/nyheter") public String newsPage(@RequestParam("page") int page, @RequestParam("category") String category, Model model){
        Pageable pageable = PageRequest.of(page, 5);
        Pageable pageablePosts = PageRequest.of(0, 20);
        Calendar cal = Calendar.getInstance();
        if(category.contentEquals("all")){
            cal.add(Calendar.YEAR, -3);
        }
        else if(category.contentEquals("v")){
            cal.add(Calendar.DATE, -8);
        }
        else if(category.contentEquals("m")){
            cal.add(Calendar.DATE, -32);
        }
        else if(category.contentEquals("y")){
            cal.add(Calendar.DATE, -370);
        }
        java.sql.Date timeInterval = new java.sql.Date(cal.getTimeInMillis());
        Page<Nyhet> nyheter = nyhetRepository.findAllByDateCreatedAfterOrderByPublishedDesc(timeInterval, pageable);
        model.addAttribute("nyheter", nyheter.getContent());
        model.addAttribute("totalHits", nyheter.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("category", category);
        Page<Post> posts = postRepository.findAllByStatusTrueAndPageOrderByPublishedDesc("Nyheter", pageablePosts);
        model.addAttribute("posts", posts);
        return "nyheter";
    }
    @GetMapping("/search_news")
    public String updateArticlesNews(Model model, HttpServletRequest request, @RequestParam("search_input") String searchInput, @RequestParam("page") int page, @RequestParam("category") String category){
        Pageable pageable = PageRequest.of(page, 5);
        Calendar cal = Calendar.getInstance();
        if(category.contentEquals("all")){
            cal.add(Calendar.YEAR, -3);
        }
        else if(category.contentEquals("v")){
            cal.add(Calendar.DATE, -8);
        }
        else if(category.contentEquals("m")){
            cal.add(Calendar.DATE, -32);
        }
        else if(category.contentEquals("y")){
            cal.add(Calendar.DATE, -370);
        }
        java.sql.Date timeInterval = new java.sql.Date(cal.getTimeInMillis());
        Page<Nyhet> nyheter = nyhetRepository.findAllByTitleContainingAndDateCreatedAfterOrderByPublishedDesc(searchInput, timeInterval, pageable);
        model.addAttribute("nyheter", nyheter.getContent());
        model.addAttribute("totalHits", nyheter.getTotalPages());
        model.addAttribute("page", page);
        return "nyheter :: .tableSearch";
    }
    @GetMapping("/search_news_home")
    public String updateArticlesNewsHome(Model model, HttpServletRequest request, @RequestParam("search_input") String searchInput, @RequestParam("page") int page, @RequestParam("category") String category){
        Pageable pageable = PageRequest.of(page, 5);
        Calendar cal = Calendar.getInstance();
        if(category.contentEquals("all")){
            cal.add(Calendar.YEAR, -3);
        }
        else if(category.contentEquals("v")){
            cal.add(Calendar.DATE, -8);
        }
        else if(category.contentEquals("m")){
            cal.add(Calendar.DATE, -32);
        }
        else if(category.contentEquals("y")){
            cal.add(Calendar.DATE, -370);
        }
        java.sql.Date timeInterval = new java.sql.Date(cal.getTimeInMillis());
        Page<Nyhet> nyheter = nyhetRepository.findAllByTitleContainingAndDateCreatedAfterOrderByPublishedDesc(searchInput, timeInterval, pageable);
        model.addAttribute("nyheter", nyheter.getContent());
        model.addAttribute("totalHits", nyheter.getTotalPages());
        model.addAttribute("page", page);
        return "hem :: .tableSearch";
    }
    @GetMapping("/om-oss") public String aboutUsPage(Model model){
        CoverImage coverImage1 = coverImageRepository.findByPageName("om-oss bild 1");
        CoverImage coverImage2 = coverImageRepository.findByPageName("om-oss bild 2");
        model.addAttribute("coverImage1", coverImage1);
        model.addAttribute("coverImage2", coverImage2);
        return "om-oss";
    }

    @GetMapping("/vardgivare") public String userPage(Model model, @RequestParam("page") int page){
        Pageable pageable = PageRequest.of(page, 10);
        Pageable pageablePosts = PageRequest.of(page, 20);
        Page<Provider> providers = providerRepository.findAllByIdIsNotNullAndHiddenIsFalseOrderBySponsoredDescPayingDescDateCreatedDesc(pageable);
        Page<Post> posts = postRepository.findAllByStatusTrueAndPageOrderByPublishedDesc("Vårdgivare", pageablePosts);
        model.addAttribute("posts", posts);
        model.addAttribute("providers", providers.getContent());
        model.addAttribute("totalProviders", providers.getTotalElements());
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
        Pageable pageablePosts = PageRequest.of(page, 20);
        Page<Provider> providers = providerRepository.findAllByNameContainingAndHiddenIsFalseAndTypeListContainingAndCountyContainingAndGradeContainingOrderBySponsoredDescPayingDescDateCreatedDesc(searchInput, branchType, county, grade, pageable);
        model.addAttribute("providers", providers.getContent());
        model.addAttribute("totalProviders", providers.getTotalElements());
        model.addAttribute("totalHits", providers.getTotalPages());
        model.addAttribute("page", page);
        Page<Post> posts = postRepository.findAllByStatusTrueAndPageOrderByPublishedDesc("Vårdgivare", pageablePosts);
        model.addAttribute("posts", posts);
        return "vardgivare :: .tableSearch";
    }
    @PostMapping("/vardgivare_search")
    public String searchProviderCustom(Model model, HttpServletRequest request){
        Pageable pageable = PageRequest.of(0, 10);
        Pageable pageablePosts = PageRequest.of(0, 20);
        String searchInput = request.getParameter("search_input");
        String branchType = request.getParameter("branchType");
        String county = request.getParameter("county");
        String grade = request.getParameter("grade");

        model.addAttribute("searchInput", searchInput);
        model.addAttribute("branchType", branchType);
        model.addAttribute("county", county);
        model.addAttribute("grade", grade);
        Page<Provider> providers = providerRepository.findAllByNameContainingAndHiddenIsFalseAndTypeListContainingAndCountyContainingAndGradeContainingOrderBySponsoredDescPayingDescDateCreatedDesc(searchInput, branchType, county, grade, pageable);
        Page<Post> posts = postRepository.findAllByStatusTrueAndPageOrderByPublishedDesc("Vårdgivare", pageablePosts);
        model.addAttribute("posts", posts);
        model.addAttribute("providers", providers.getContent());
        model.addAttribute("totalProviders", providers.getTotalElements());
        model.addAttribute("totalHits", providers.getTotalPages());
        model.addAttribute("page", 0);
        return "vardgivare";
    }
    @GetMapping("/vardgivare/{id}") public String userPageNew(@PathVariable long id, Model model){
        Provider provider = providerRepository.findById(id);
        ArrayList<UserDocs> userDocs = userDocsRepository.findAllByProviderId(provider.getId());
        ArrayList<UserQualityTales> userQualityTales = userQualityTalesRepository.findAllByProviderId(provider.getId());
        ArrayList<UserRegistry> userRegistries = userRegistryRepository.findAllByProviderId(provider.getId());
        ArrayList<UserReports> userReports = userReportsRepository.findAllByProviderId(provider.getId());
        ArrayList<SlideshowDocs> slideshowDocs = slideshowDocsRepository.findAllByProviderId(provider.getId());
        model.addAttribute("slideshowDocs", slideshowDocs);
        model.addAttribute("provider", provider);

        model.addAttribute("userDocs", userDocs);
        model.addAttribute("userQualityTales", userQualityTales);
        model.addAttribute("userRegistries", userRegistries);
        model.addAttribute("userReports", userReports);
        return "vardgivare-template";
    }
    @GetMapping("vardgivare_search") public String vardgivareRedirect(){
        return "redirect:/vardgivare?page=0";
    }

}
