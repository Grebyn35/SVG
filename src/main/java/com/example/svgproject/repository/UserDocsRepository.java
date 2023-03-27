package com.example.svgproject.repository;

import com.example.svgproject.model.User;
import com.example.svgproject.model.UserDocs;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface UserDocsRepository extends CrudRepository<UserDocs,Long> {

    UserDocs findById(long id);
    ArrayList<UserDocs> findAll();
}
