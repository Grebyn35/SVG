package com.example.svgproject.controller;

import com.example.svgproject.model.Provider;
import com.example.svgproject.model.User;
import com.example.svgproject.repository.ProviderRepository;
import com.example.svgproject.repository.UserRepository;
import com.uploadcare.api.Client;
import com.uploadcare.api.File;
import com.uploadcare.upload.FileUploader;
import com.uploadcare.upload.UploadFailureException;
import com.uploadcare.upload.Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;

@Controller
public class AdminController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ProviderRepository providerRepository;

    @GetMapping("/admin/annonser") public String adminPosts(){
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
    @RequestMapping(value=("/admin/ny-vardgivare"),headers=("content-type=multipart/*"),method=RequestMethod.POST) public String adminNewUserPost(Model model, HttpServletRequest request, @RequestParam("imgLogo") MultipartFile imgLogo, @RequestParam("logoQualityTale") MultipartFile logoQualityTale, @RequestParam("imgBrochure") MultipartFile imgBrochure, @RequestParam("imgExtraInfo") MultipartFile imgExtraInfo, @Valid @ModelAttribute("providerObject") Provider provider) throws IOException {
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
                System.out.println("ADDING " + gradeValues[i]);
                gradeList.add(gradeValues[i]);
            }
        }
        else{
            System.out.println("ADDING " + "[]");
            gradeList.add("[]");
        }
        provider.setGrade(gradeList.toString());
        provider.setTypeList(typeList.toString());
        provider.setOtherSettings(otherSettingsList.toString());
        if(imgLogo.getSize()>10){
            String logoSrc = uploadFileToServer(imgLogo);
            provider.setLogoSrc(logoSrc);
        }
        if(logoQualityTale.getSize()>10){
            String qualityTaleSrc = uploadFileToServer(logoQualityTale);
            provider.setQualityTaleSrc(qualityTaleSrc);
        }
        if(imgBrochure.getSize()>10){
            String brochureSrc = uploadFileToServer(imgBrochure);
            provider.setBrochureSrc(brochureSrc);
        }
        if(imgExtraInfo.getSize()>10){
            String extraInfoSrc = uploadFileToServer(imgExtraInfo);
            provider.setExtraInfoSrc(extraInfoSrc);
        }
        providerRepository.save(provider);
        return "redirect:/admin/start";
    }
    @GetMapping("/admin/nyheter") public String adminNews(){
        return "admin-nyheter";
    }
    @GetMapping("/admin/start") public String adminStart(Model model){
        addAdminStartAttributes(model);
        return "admin-start";
    }
    public void addAdminStartAttributes(Model model){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Provider> providers = providerRepository.findAll(pageable);
        model.addAttribute("providers", providers.getContent());
    }
    @GetMapping("/admin/vardgivare") public String adminVardgivarePage(Model model, @RequestParam("page") int page){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Provider> providers = providerRepository.findAll(pageable);
        model.addAttribute("providers", providers.getContent());
        model.addAttribute("totalHits", providers.getTotalPages());
        model.addAttribute("page", page);
        return "admin-vardgivare";
    }

    @GetMapping("/admin/redigera-annons/{id}") public String adminEditPost(@PathVariable long id){
        return "admin-redigera-annons";
    }
    @GetMapping("/admin/redigera-nyhet/{id}") public String adminEditNews(@PathVariable long id){
        return "admin-redigera-nyhet";
    }
    @GetMapping("/admin/redigera-vardgivare/{id}") public String editProvider(@PathVariable long id, Model model){
        Provider provider = providerRepository.findById(id);
        model.addAttribute("provider", provider);
        return "admin-redigera-vardgivare";
    }
    @RequestMapping(value=("/admin/redigera-vardgivare/{id}"),headers=("content-type=multipart/*"),method=RequestMethod.POST) public String editProviderPost(@PathVariable long id, Model model, HttpServletRequest request, @RequestParam("imgLogo") MultipartFile imgLogo, @RequestParam("logoQualityTale") MultipartFile logoQualityTale, @RequestParam("imgBrochure") MultipartFile imgBrochure, @RequestParam("imgExtraInfo") MultipartFile imgExtraInfo) throws IOException {
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
        provider.setName(request.getParameter("name"));
        provider.setCounty(request.getParameter("county"));
        provider.setEmail(request.getParameter("email"));
        provider.setWebsite(request.getParameter("website"));
        provider.setAbout(request.getParameter("about"));
        provider.setRemark(request.getParameter("remark"));
        if(imgLogo.getSize()>10){
            String logoSrc = uploadFileToServer(imgLogo);
            provider.setLogoSrc(logoSrc);
        }
        if(logoQualityTale.getSize()>10){
            String qualityTaleSrc = uploadFileToServer(logoQualityTale);
            provider.setQualityTaleSrc(qualityTaleSrc);
        }
        if(imgBrochure.getSize()>10){
            String brochureSrc = uploadFileToServer(imgBrochure);
            provider.setBrochureSrc(brochureSrc);
        }
        if(imgExtraInfo.getSize()>10){
            String extraInfoSrc = uploadFileToServer(imgExtraInfo);
            provider.setExtraInfoSrc(extraInfoSrc);
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
    @PostMapping("/admin/delete-provider")
    public String deleteProvider(Model model, HttpServletRequest request){
        long id = Long.parseLong(request.getParameter("id"));
        Provider provider = providerRepository.findById(id);
        providerRepository.delete(provider);
        return "redirect:/admin/vardgivare?page=0";
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
}
