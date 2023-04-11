package com.example.svgproject.repository;

import com.example.svgproject.model.UserRegistry;
import com.example.svgproject.model.UserReports;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface UserReportsRepository extends CrudRepository<UserReports,Long> {

    UserReports findById(long id);
    ArrayList<UserReports> findAll();
    ArrayList<UserReports> findAllByProviderId(long id);
}