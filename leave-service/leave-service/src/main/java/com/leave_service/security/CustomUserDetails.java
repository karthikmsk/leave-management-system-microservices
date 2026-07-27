package com.leave_service.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private Long employeeId;

    private String username;

    private Collection<? extends GrantedAuthority> authorities;

    public Collection<? extends GrantedAuthority> getAuthorities(){
        return authorities;
    }

    public CustomUserDetails(Long employeeId, String username, Collection<? extends GrantedAuthority> authorities){
        this.employeeId = employeeId;
        this.username = username;
        this.authorities = authorities;
    }

    public Long getEmployeeId(){
        return employeeId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword(){
        return null;    
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
    public boolean isEnabled(){
        return true;
    }

}
