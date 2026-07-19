package com.user_service.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.user_service.model.User;
import com.user_service.security.CustomUserDetails;
import com.user_service.security.CustomUserDetailsService;
import com.user_service.security.JwtService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthMapper mapper;
    private final JwtService jwtService;
    
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        User user = customUserDetails.getUser();
        String token = jwtService.generateToken(user);
        LoginResponse response = mapper.toLoginResponse(user);
        response.setMessage("Login Successful");
        response.setToken(token);
        return response;
    }

}
