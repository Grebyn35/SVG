package com.example.svgproject.security;

import com.example.svgproject.model.User;
import com.example.svgproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.example.svgproject.security.CustomUserDetails;


public class CustomService implements UserDetailsService {

    @Autowired
    UserRepository repository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user  = repository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found") ;
        }
        CustomUserDetails customUser= new CustomUserDetails(user);
        return customUser;
    }


}
