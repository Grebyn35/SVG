package com.example.svgproject.controller;

import com.example.svgproject.model.*;
import com.example.svgproject.repository.*;
import com.example.svgproject.security.CustomUserDetails;
import com.uploadcare.api.Client;
import com.uploadcare.api.File;
import com.uploadcare.upload.FileUploader;
import com.uploadcare.upload.UploadFailureException;
import com.uploadcare.upload.Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

@Controller
public class AdminController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    NyhetRepository nyhetRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    SlideshowDocsRepository slideshowDocsRepository;

    @Autowired
    UserDocsRepository userDocsRepository;
    @Autowired
    UserQualityTalesRepository userQualityTalesRepository;
    @Autowired
    UserRegistryRepository userRegistryRepository;
    @Autowired
    UserReportsRepository userReportsRepository;

    @GetMapping("/admin/annonser") public String adminPosts(Model model, @RequestParam("page") int page){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Post> posts = postRepository.findAllByIdIsNotNullOrderByPublishedDesc(pageable);
        model.addAttribute("posts", posts.getContent());
        model.addAttribute("totalHits", posts.getTotalPages());
        model.addAttribute("page", page);
        return "admin-annonser";
    }
    @GetMapping("/admin/ny-annons") public String adminNewPost(){
        return "admin-ny-annons";
    }
    @GetMapping("/admin/ny-nyhet") public String adminNewNews(){
        return "admin-ny-nyhet";
    }
    @GetMapping("/admin/ny-vardgivare") public String adminNewUser(Model model){
        model.addAttribute("providerObject", new Provider());
        return "admin-ny-vardgivare";
    }
    @RequestMapping(value=("/admin/ny-vardgivare"),headers=("content-type=multipart/*"),method=RequestMethod.POST) public String adminNewUserPost(Model model, HttpServletRequest request, @RequestParam("coordinatorImage") MultipartFile coordinatorImage, @RequestParam("imgLogo") MultipartFile imgLogo) throws IOException {
        Provider provider = new Provider();
        ArrayList<String> typeList = new ArrayList<>();
        ArrayList<String> otherSettingsList = new ArrayList<>();
        ArrayList<String> gradeList = new ArrayList<>();
        String[] typeListValues = request.getParameterValues("typeList[]");
        String[] otherSettingsValues = request.getParameterValues("otherSettings[]");
        String[] gradeValues = request.getParameterValues("grade[]");
        if(typeListValues != null){
            for(int i = 0; i<typeListValues.length;i++){
                typeList.add(typeListValues[i]);
            }
        }
        else{
            typeList.add("[]");
        }
        if(otherSettingsValues != null){
            for(int i = 0; i<otherSettingsValues.length;i++){
                otherSettingsList.add(otherSettingsValues[i]);
            }
        }
        else{
            otherSettingsList.add("[]");
        }
        if(gradeValues != null){
            for(int i = 0; i<gradeValues.length;i++){
                gradeList.add(gradeValues[i]);
            }
        }
        else{
            gradeList.add("[]");
        }
        provider.setGrade(gradeList.toString());
        provider.setTypeList(typeList.toString());
        provider.setOtherSettings(otherSettingsList.toString());
        provider.setDateCreated(returnDateWithTime());

        provider.setName(request.getParameter("name"));
        provider.setOrgNr(request.getParameter("orgNr"));
        provider.setCounty(request.getParameter("county"));
        provider.setEmail(request.getParameter("email"));
        provider.setTel(request.getParameter("tel"));
        provider.setWebsite(request.getParameter("website"));

        provider.setCoordinatorRole(request.getParameter("coordinatorRole"));
        provider.setHasPermission(request.getParameter("hasPermission"));
        provider.setNoPermission(request.getParameter("noPermission"));
        provider.setCmpAdress(request.getParameter("cmpAdress"));

        provider.setAbout(request.getParameter("about"));
        provider.setCoordinatorName(request.getParameter("coordinatorName"));
        provider.setRemark(request.getParameter("remark"));
        provider.setInformation(request.getParameter("information"));
        provider.setOrientation(request.getParameter("orientation"));

        provider.setContribution(request.getParameter("contribution"));
        provider.setMethods(request.getParameter("methods"));
        provider.setOrientation(request.getParameter("orientation"));
        if(imgLogo.getSize()>10){
            String logoSrc = uploadFileToServer(imgLogo);
            provider.setLogoSrc(logoSrc);
        }
        if(coordinatorImage.getSize()>10){
            String coordinatorImageSrc = uploadFileToServer(coordinatorImage);
            provider.setCoordinatorImage(coordinatorImageSrc);
        }
        providerRepository.save(provider);
        return "redirect:/admin/start";
    }
    @RequestMapping(value=("/admin/ny-nyhet"),headers=("content-type=multipart/*"),method=RequestMethod.POST) public String adminNewNews(Model model, HttpServletRequest request, @RequestParam("coverImg") MultipartFile coverImg) throws IOException {
        Nyhet nyhet = new Nyhet();
        nyhet.setTitle(request.getParameter("title"));
        nyhet.setCategory(request.getParameter("category"));
        nyhet.setContent(request.getParameter("content"));
        nyhet.setPublished(returnDateWithTime());
        if(coverImg.getSize()>10){
            String coverImgSrc = uploadFileToServer(coverImg);
            nyhet.setCoverImgSrc(coverImgSrc);
        }
        nyhetRepository.save(nyhet);
        return "redirect:/admin/start";
    }
    @RequestMapping(value=("/admin/ny-annons"),headers=("content-type=multipart/*"),method=RequestMethod.POST) public String adminNewPost(Model model, HttpServletRequest request, @RequestParam("imgLogo") MultipartFile imgLogo) throws IOException {
        Post post = new Post();
        String[] status = request.getParameterValues("status[]");
        post.setLink(request.getParameter("link"));
        post.setContent(request.getParameter("content"));
        post.setPublished(returnDateWithTime());
        post.setName(request.getParameter("name"));
        post.setEmail(request.getParameter("email"));
        post.setStatus(Boolean.parseBoolean(status[0]));
        if(imgLogo.getSize()>10){
            String coverImgSrc = uploadFileToServer(imgLogo);
            post.setCoverImgSrc(coverImgSrc);
        }
        postRepository.save(post);
        return "redirect:/admin/start";
    }
    @RequestMapping(value=("/admin/redigera-annons/{id}"),headers=("content-type=multipart/*"),method=RequestMethod.POST) public String adminEditPost(Model model, HttpServletRequest request, @RequestParam("imgLogo") MultipartFile imgLogo, @PathVariable long id) throws IOException {
        Post post = postRepository.findById(id);
        String[] status = request.getParameterValues("status[]");
        post.setLink(request.getParameter("link"));
        post.setContent(request.getParameter("content"));
        post.setName(request.getParameter("name"));
        post.setEmail(request.getParameter("email"));
        post.setStatus(Boolean.parseBoolean(status[0]));
        if(imgLogo.getSize()>10){
            String coverImgSrc = uploadFileToServer(imgLogo);
            post.setCoverImgSrc(coverImgSrc);
        }
        postRepository.save(post);
        return "redirect:/admin/annonser?page=0";
    }
    @RequestMapping(value=("/admin/redigera-nyhet/{id}"),headers=("content-type=multipart/*"),method=RequestMethod.POST) public String adminNewNews(Model model, HttpServletRequest request, @RequestParam("coverImg") MultipartFile coverImg, @PathVariable long id) throws IOException {
        Nyhet nyhet = nyhetRepository.findById(id);
        nyhet.setTitle(request.getParameter("title"));
        nyhet.setCategory(request.getParameter("category"));
        nyhet.setContent(request.getParameter("content"));
        if(coverImg.getSize()>10){
            String coverImgSrc = uploadFileToServer(coverImg);
            nyhet.setCoverImgSrc(coverImgSrc);
        }
        nyhetRepository.save(nyhet);
        return "redirect:/admin/nyheter?page=0";
    }
    public String returnDateWithTime(){
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));
        return df.format(date);
    }
    @GetMapping("/admin/nyheter") public String adminNews(@RequestParam("page") int page, Model model){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Nyhet> nyheter = nyhetRepository.findAllByIdIsNotNullOrderByPublishedDesc(pageable);
        model.addAttribute("nyheter", nyheter.getContent());
        model.addAttribute("totalHits", nyheter.getTotalPages());
        model.addAttribute("page", page);
        return "admin-nyheter";
    }
    @GetMapping("/admin/start") public String adminStart(Model model){
        addAdminStartAttributes(model);
        return "admin-start";
    }
    public void addAdminStartAttributes(Model model){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Provider> providers = providerRepository.findAllByIdIsNotNullOrderByDateCreatedDesc(pageable);
        Page<Nyhet> nyheter = nyhetRepository.findAllByIdIsNotNullOrderByPublishedDesc(pageable);
        Page<Post> posts = postRepository.findAllByIdIsNotNullOrderByPublishedDesc(pageable);
        model.addAttribute("providers", providers.getContent());
        model.addAttribute("nyheter", nyheter.getContent());
        model.addAttribute("posts", posts.getContent());
        model.addAttribute("user", returnCurrentUser());
    }
    public User returnCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUser = (CustomUserDetails) auth.getPrincipal();
        User currentUser = userRepository.findByEmail(customUser.getUsername());
        return currentUser;
    }
    @GetMapping("/admin/vardgivare") public String adminVardgivarePage(Model model, @RequestParam("page") int page){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Provider> providers = providerRepository.findAllByIdIsNotNullOrderByDateCreatedDesc(pageable);
        model.addAttribute("providers", providers.getContent());
        model.addAttribute("totalHits", providers.getTotalPages());
        model.addAttribute("page", page);
        return "admin-vardgivare";
    }

    @GetMapping("/admin/redigera-annons/{id}") public String adminEditPost(@PathVariable long id, Model model){
        Post post = postRepository.findById(id);
        model.addAttribute("post", post);
        return "admin-redigera-annons";
    }
    @GetMapping("/admin/redigera-nyhet/{id}") public String adminEditNews(@PathVariable long id, Model model){
        Nyhet nyhet = nyhetRepository.findById(id);
        model.addAttribute("nyhet", nyhet);
        return "admin-redigera-nyhet";
    }
    @GetMapping("/admin/redigera-vardgivare/{id}") public String editProvider(@PathVariable long id, Model model){
        Provider provider = providerRepository.findById(id);
        model.addAttribute("provider", provider);
        return "admin-redigera-vardgivare";
    }
    @RequestMapping(value=("/admin/redigera-vardgivare/{id}"),headers=("content-type=multipart/*"),method=RequestMethod.POST) public String editProviderPost(@PathVariable long id, Model model, @RequestParam("coordinatorImage") MultipartFile coordinatorImage, HttpServletRequest request, @RequestParam("imgLogo") MultipartFile imgLogo) throws IOException {
        Provider provider = providerRepository.findById(id);
        ArrayList<String> typeList = new ArrayList<>();
        ArrayList<String> otherSettingsList = new ArrayList<>();
        ArrayList<String> gradeList = new ArrayList<>();
        String[] typeListValues = request.getParameterValues("typeList[]");
        String[] otherSettingsValues = request.getParameterValues("otherSettings[]");
        String[] gradeValues = request.getParameterValues("grade[]");
        if(typeListValues != null){
            for(int i = 0; i<typeListValues.length;i++){
                typeList.add(typeListValues[i]);
            }
        }
        else{
            typeList.add("[]");
        }
        if(otherSettingsValues != null){
            for(int i = 0; i<otherSettingsValues.length;i++){
                otherSettingsList.add(otherSettingsValues[i]);
            }
        }
        else{
            otherSettingsList.add("[]");
        }
        if(gradeValues != null){
            for(int i = 0; i<gradeValues.length;i++){
                gradeList.add(gradeValues[i]);
            }
        }
        else{
            gradeList.add("[]");
        }
        provider.setGrade(gradeList.toString());
        provider.setTypeList(typeList.toString());
        provider.setOtherSettings(otherSettingsList.toString());
        provider.setEdited(returnDateWithTime());

        provider.setName(request.getParameter("name"));
        provider.setOrgNr(request.getParameter("orgNr"));
        provider.setCounty(request.getParameter("county"));
        provider.setEmail(request.getParameter("email"));
        provider.setTel(request.getParameter("tel"));
        provider.setWebsite(request.getParameter("website"));

        provider.setCoordinatorRole(request.getParameter("coordinatorRole"));
        provider.setHasPermission(request.getParameter("hasPermission"));
        provider.setNoPermission(request.getParameter("noPermission"));
        provider.setCmpAdress(request.getParameter("cmpAdress"));

        provider.setAbout(request.getParameter("about"));
        provider.setCoordinatorName(request.getParameter("coordinatorName"));
        provider.setRemark(request.getParameter("remark"));
        provider.setInformation(request.getParameter("information"));
        provider.setOrientation(request.getParameter("orientation"));

        provider.setContribution(request.getParameter("contribution"));
        provider.setMethods(request.getParameter("methods"));
        provider.setOrientation(request.getParameter("orientation"));
        if(imgLogo.getSize()>10){
            String logoSrc = uploadFileToServer(imgLogo);
            provider.setLogoSrc(logoSrc);
        }
        if(coordinatorImage.getSize()>10){
            String coordinatorImageSrc = uploadFileToServer(coordinatorImage);
            provider.setCoordinatorImage(coordinatorImageSrc);
        }
        providerRepository.save(provider);
        return "redirect:/admin/vardgivare?page=0";
    }
    @GetMapping("/admin/search_vardgivare")
    public String updateArticles(Model model, HttpServletRequest request, @RequestParam("search_input") String searchInput, @RequestParam("branch_type") String branchType, @RequestParam("grade") String grade, @RequestParam("page") int page, @RequestParam("county") String county){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Provider> providers = providerRepository.findAllByNameContainingAndTypeListContainingAndCountyContainingAndGradeContaining(searchInput, branchType, county, grade, pageable);
        model.addAttribute("providers", providers.getContent());
        model.addAttribute("totalHits", providers.getTotalPages());
        model.addAttribute("page", page);
        return "admin-vardgivare :: .tableSearch";
    }
    @GetMapping("/admin/search_news")
    public String updateArticlesNews(Model model, HttpServletRequest request, @RequestParam("search_input") String searchInput, @RequestParam("page") int page, @RequestParam("category") String category){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Nyhet> nyheter = nyhetRepository.findAllByTitleContainingAndCategoryContainingOrderByPublishedDesc(searchInput, category, pageable);
        model.addAttribute("nyheter", nyheter.getContent());
        model.addAttribute("totalHits", nyheter.getTotalPages());
        model.addAttribute("page", page);
        return "admin-nyheter :: .tableSearch";
    }
    @PostMapping("/admin/delete-provider")
    public String deleteProvider(Model model, HttpServletRequest request){
        long id = Long.parseLong(request.getParameter("id"));
        Provider provider = providerRepository.findById(id);
        providerRepository.delete(provider);
        return "redirect:/admin/vardgivare?page=0";
    }
    @PostMapping("/admin/delete-news")
    public String deleteNews(Model model, HttpServletRequest request){
        long id = Long.parseLong(request.getParameter("id"));
        Nyhet nyhet = nyhetRepository.findById(id);
        nyhetRepository.delete(nyhet);
        return "redirect:/admin/nyheter?page=0";
    }
    @PostMapping("/admin/delete-post")
    public String deletePost(Model model, HttpServletRequest request){
        long id = Long.parseLong(request.getParameter("id"));
        Post post = postRepository.findById(id);
        postRepository.delete(post);
        return "redirect:/admin/annonser?page=0";
    }
    public String uploadFileToServer(MultipartFile uploadedFile) throws IOException {
        Client client = new Client("eca459e5791d32ddb0f4", "78c7be5af84b70995435");
        Uploader uploader = new FileUploader(client,new BufferedInputStream(uploadedFile.getInputStream()), uploadedFile.getOriginalFilename());
        try {
            File savedFile = uploader.upload().save();
            return savedFile.getOriginalFileUrl().toString();
        } catch (UploadFailureException e) {
            e.printStackTrace();
        }
        return null;
    }
    @GetMapping("/admin/redigera-dokument/{id}")
    public String editDoc(Model model, HttpServletRequest request, @PathVariable long id){
        Provider provider = providerRepository.findById(id);
        ArrayList<UserDocs> userDocs = userDocsRepository.findAllByProviderId(provider.getId());
        model.addAttribute("userDocs", userDocs);
        model.addAttribute("provider", provider);
        return "admin-redigera-dokument";
    }
    @GetMapping("/admin/redigera-bildspel/{id}")
    public String editSlideshow(Model model, HttpServletRequest request, @PathVariable long id){
        Provider provider = providerRepository.findById(id);
        ArrayList<SlideshowDocs> slideshowDocs = slideshowDocsRepository.findAllByProviderId(provider.getId());
        model.addAttribute("slideshowDocs", slideshowDocs);
        model.addAttribute("provider", provider);
        return "admin-redigera-bildspel";
    }
    @PostMapping("/admin/radera-dokument")
    public String deleteDoc(Model model, HttpServletRequest request){
        long id = Long.parseLong(request.getParameter("id"));
        UserDocs userDocs = userDocsRepository.findById(id);
        long providerId = userDocs.getProviderId();
        userDocsRepository.delete(userDocs);
        return "redirect:/admin/redigera-dokument/" + providerId;
    }
    @PostMapping("/admin/radera-bildspel")
    public String deleteSlideshow(Model model, HttpServletRequest request){
        long id = Long.parseLong(request.getParameter("id"));
        SlideshowDocs slideshowDocs = slideshowDocsRepository.findById(id);
        long providerId = slideshowDocs.getProviderId();
        slideshowDocsRepository.delete(slideshowDocs);
        return "redirect:/admin/redigera-bildspel/" + providerId;
    }
    @PostMapping("/admin/radera-kvalitetsberattelser")
    public String deleteQualitytales(Model model, HttpServletRequest request){
        long id = Long.parseLong(request.getParameter("id"));
        UserQualityTales userQualityTales = userQualityTalesRepository.findById(id);
        long providerId = userQualityTales.getProviderId();
        userQualityTalesRepository.delete(userQualityTales);
        return "redirect:/admin/redigera-kvalitetsberattelser/" + providerId;
    }
    @PostMapping("/admin/radera-tillstandsbevis")
    public String deleteTillstandsbevis(Model model, HttpServletRequest request){
        long id = Long.parseLong(request.getParameter("id"));
        UserRegistry userRegistry = userRegistryRepository.findById(id);
        long providerId = userRegistry.getProviderId();
        userRegistryRepository.delete(userRegistry);
        return "redirect:/admin/redigera-tillstandsbevis/" + providerId;
    }
    @PostMapping("/admin/radera-tillsynsrapporter")
    public String deleteTillsynsrapport(Model model, HttpServletRequest request){
        long id = Long.parseLong(request.getParameter("id"));
        UserReports userReports = userReportsRepository.findById(id);
        long providerId = userReports.getProviderId();
        userReportsRepository.delete(userReports);
        return "redirect:/admin/redigera-tillsynsrapporter/" + providerId;
    }
    @PostMapping("/admin/redigera-dokument-namn")
    public String editDocName(Model model, HttpServletRequest request){
        UserDocs userDocs = userDocsRepository.findById(Long.parseLong(request.getParameter("id")));
        userDocs.setName(request.getParameter("name"));
        userDocsRepository.save(userDocs);
        return "redirect:/admin/redigera-dokument/" + userDocs.getProviderId();
    }
    @PostMapping("/admin/redigera-bildspel-namn")
    public String editSlideshowName(Model model, HttpServletRequest request){
        SlideshowDocs slideshowDocs = slideshowDocsRepository.findById(Long.parseLong(request.getParameter("id")));
        slideshowDocs.setName(request.getParameter("name"));
        slideshowDocsRepository.save(slideshowDocs);
        return "redirect:/admin/redigera-bildspel/" + slideshowDocs.getProviderId();
    }
    @RequestMapping(value=("/admin/redigera-dokument/{id}"),headers=("content-type=multipart/*"),method=RequestMethod.POST) public String editDocPost(@PathVariable long id, Model model, @RequestParam("userFile") MultipartFile userFile) throws IOException {
        UserDocs userDocs = new UserDocs();
        if(userFile.getSize()>10){
            String logoSrc = uploadFileToServer(userFile);
            userDocs.setName(userFile.getOriginalFilename());
            userDocs.setSrc(logoSrc);
            userDocs.setProviderId(id);
            userDocs.setCreated(returnDateWithTime());
            userDocsRepository.save(userDocs);
        }
        return "redirect:/admin/redigera-vardgivare/" + id;
    }
    @RequestMapping(value=("/admin/redigera-bildspel/{id}"),headers=("content-type=multipart/*"),method=RequestMethod.POST) public String editSlideshowPost(@PathVariable long id, Model model, @RequestParam("userFile") MultipartFile userFile) throws IOException {
        SlideshowDocs slideshowDocs = new SlideshowDocs();
        if(userFile.getSize()>10){
            String logoSrc = uploadFileToServer(userFile);
            slideshowDocs.setName(userFile.getOriginalFilename());
            slideshowDocs.setSrc(logoSrc);
            slideshowDocs.setProviderId(id);
            slideshowDocs.setCreated(returnDateWithTime());
            slideshowDocsRepository.save(slideshowDocs);
        }
        return "redirect:/admin/redigera-bildspel/" + id;
    }
    @GetMapping("/admin/redigera-kvalitetsberattelser/{id}")
    public String editKvalitetsberattelser(Model model, HttpServletRequest request, @PathVariable long id){
        Provider provider = providerRepository.findById(id);
        ArrayList<UserQualityTales> userQualityTales = userQualityTalesRepository.findAllByProviderId(provider.getId());
        model.addAttribute("userQualityTales", userQualityTales);
        model.addAttribute("provider", provider);
        return "admin-redigera-kvalitetsberattelser";
    }
    @PostMapping("/admin/redigera-kvalitetsberattelser-namn")
    public String editKvalitetsberattelserName(Model model, HttpServletRequest request){
        UserQualityTales userQualityTales = userQualityTalesRepository.findById(Long.parseLong(request.getParameter("id")));
        userQualityTales.setName(request.getParameter("name"));
        userQualityTalesRepository.save(userQualityTales);
        return "redirect:/admin/redigera-kvalitetsberattelser/" + userQualityTales.getProviderId();
    }
    @RequestMapping(value=("/admin/redigera-kvalitetsberattelser/{id}"),headers=("content-type=multipart/*"),method=RequestMethod.POST) public String editKvalitetsberattelserPost(@PathVariable long id, Model model, @RequestParam("userFile") MultipartFile userFile) throws IOException {
        UserQualityTales userQualityTales = new UserQualityTales();
        if(userFile.getSize()>10){
            String logoSrc = uploadFileToServer(userFile);
            userQualityTales.setName(userFile.getOriginalFilename());
            userQualityTales.setSrc(logoSrc);
            userQualityTales.setProviderId(id);
            userQualityTales.setCreated(returnDateWithTime());
            userQualityTalesRepository.save(userQualityTales);
        }
        return "redirect:/admin/redigera-vardgivare/" + id;
    }
    @GetMapping("/admin/redigera-tillstandsbevis/{id}")
    public String editTillstandsbevis(Model model, HttpServletRequest request, @PathVariable long id){
        Provider provider = providerRepository.findById(id);
        ArrayList<UserRegistry> userRegistries = userRegistryRepository.findAllByProviderId(provider.getId());
        model.addAttribute("userRegistries", userRegistries);
        model.addAttribute("provider", provider);
        return "admin-redigera-tillstandsbevis";
    }
    @PostMapping("/admin/redigera-tillstandsbevis-namn")
    public String editTillstandsbevisName(Model model, HttpServletRequest request){
        UserRegistry userRegistry = userRegistryRepository.findById(Long.parseLong(request.getParameter("id")));
        userRegistry.setName(request.getParameter("name"));
        userRegistryRepository.save(userRegistry);
        return "redirect:/admin/redigera-tillstandsbevis/" + userRegistry.getProviderId();
    }
    @RequestMapping(value=("/admin/redigera-tillstandsbevis/{id}"),headers=("content-type=multipart/*"),method=RequestMethod.POST) public String editTillstandsbevisPost(@PathVariable long id, Model model, @RequestParam("userFile") MultipartFile userFile) throws IOException {
        UserRegistry userRegistry = new UserRegistry();
        if(userFile.getSize()>10){
            String logoSrc = uploadFileToServer(userFile);
            userRegistry.setName(userFile.getOriginalFilename());
            userRegistry.setSrc(logoSrc);
            userRegistry.setProviderId(id);
            userRegistry.setCreated(returnDateWithTime());
            userRegistryRepository.save(userRegistry);
        }
        return "redirect:/admin/redigera-vardgivare/" + id;
    }
    @GetMapping("/admin/redigera-tillsynsrapporter/{id}")
    public String editTillsynsrapport(Model model, HttpServletRequest request, @PathVariable long id){
        Provider provider = providerRepository.findById(id);
        ArrayList<UserReports> userReports = userReportsRepository.findAllByProviderId(provider.getId());
        model.addAttribute("userReports", userReports);
        model.addAttribute("provider", provider);
        return "admin-redigera-tillsynsrapporter";
    }
    @PostMapping("/admin/redigera-tillsynsrapporter-namn")
    public String editTillsynsrapportName(Model model, HttpServletRequest request){
        UserReports userReports = userReportsRepository.findById(Long.parseLong(request.getParameter("id")));
        userReports.setName(request.getParameter("name"));
        userReportsRepository.save(userReports);
        return "redirect:/admin/redigera-tillsynsrapporter/" + userReports.getProviderId();
    }
    @RequestMapping(value=("/admin/redigera-tillsynsrapporter/{id}"),headers=("content-type=multipart/*"),method=RequestMethod.POST) public String editTillsynsrapportPost(@PathVariable long id, Model model, @RequestParam("userFile") MultipartFile userFile) throws IOException {
        UserReports userReports = new UserReports();
        if(userFile.getSize()>10){
            String logoSrc = uploadFileToServer(userFile);
            userReports.setName(userFile.getOriginalFilename());
            userReports.setSrc(logoSrc);
            userReports.setProviderId(id);
            userReports.setCreated(returnDateWithTime());
            userReportsRepository.save(userReports);
        }
        return "redirect:/admin/redigera-vardgivare/" + id;
    }
}
