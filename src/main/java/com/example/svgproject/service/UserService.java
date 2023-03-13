package com.example.svgproject.service;

import com.example.svgproject.model.User;

import java.util.List;

public interface UserService {
    public void save(User user);
    public String enCryptedPassword(User user);
    public List<User> getAll();
}
