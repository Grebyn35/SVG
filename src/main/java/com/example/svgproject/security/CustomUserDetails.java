package com.example.svgproject.security;

import java.util.Collection;


import com.example.svgproject.model.User;
import org.springframework.security.core.GrantedAuthority;
import java.util.Arrays;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails extends User implements UserDetails {

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }


    //@Override
    public Collection<GrantedAuthority> getAuthorities() {

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());

        return Arrays.asList(authority);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }
    public long getUserId(){
        return user.getId();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
