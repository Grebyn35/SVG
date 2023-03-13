package com.example.svgproject.service;

import com.example.svgproject.model.User;
import com.example.svgproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.svgproject.service.UserService;

import java.util.List;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private BCryptPasswordEncoder encrypt;
    @Autowired
    private UserRepository repo;

    @Override
    public void save(User user) {
        repo.save(user);
    }

    @Override
    public String enCryptedPassword(User user) {
        return encrypt.encode(user.getPassword());
    }

    @Override
    public List<User> getAll() {
        return (List<User>) repo.findAll();
    }
}
