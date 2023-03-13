package com.example.svgproject.repository;

import com.example.svgproject.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface UserRepository extends CrudRepository<User,Long> {
    User findByEmail(String email);
    User findById(long id);
    ArrayList<User> findAllByRole(String role);
}
