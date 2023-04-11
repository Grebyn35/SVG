package com.example.svgproject.repository;

import com.example.svgproject.model.UserDocs;
import com.example.svgproject.model.UserQualityTales;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface UserQualityTalesRepository extends CrudRepository<UserQualityTales,Long> {

    UserQualityTales findById(long id);
    ArrayList<UserQualityTales> findAll();
    ArrayList<UserQualityTales> findAllByProviderId(long id);
}