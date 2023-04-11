package com.example.svgproject.repository;

import com.example.svgproject.model.UserQualityTales;
import com.example.svgproject.model.UserRegistry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface UserRegistryRepository extends CrudRepository<UserRegistry,Long> {

    UserRegistry findById(long id);
    ArrayList<UserRegistry> findAll();
    ArrayList<UserRegistry> findAllByProviderId(long id);
}
