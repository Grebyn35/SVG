package com.example.svgproject.repository;

import com.example.svgproject.model.CoverImage;
import com.example.svgproject.model.UserDocs;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface CoverImageRepository extends CrudRepository<CoverImage,Long> {

    CoverImage findByPageName(String page);
    CoverImage findById(long id);
    ArrayList<CoverImage> findAll();
}