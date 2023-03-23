package com.example.svgproject.repository;

import com.example.svgproject.model.NewsLetter;
import com.example.svgproject.model.Nyhet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface NewsLetterRepository extends CrudRepository<NewsLetter,Long> {

    NewsLetter findById(long id);
    NewsLetter findByEmail(String email);
    ArrayList<NewsLetter> findAll();
}