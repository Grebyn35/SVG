package com.example.svgproject.repository;

import com.example.svgproject.model.SlideshowDocs;
import com.example.svgproject.model.UserDocs;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface SlideshowDocsRepository extends CrudRepository<SlideshowDocs,Long> {

    SlideshowDocs findById(long id);
    ArrayList<SlideshowDocs> findAll();
    ArrayList<SlideshowDocs> findAllByProviderId(long id);
}