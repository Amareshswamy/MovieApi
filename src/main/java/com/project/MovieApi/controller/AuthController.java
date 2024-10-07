package com.project.MovieApi.controller;

import com.project.MovieApi.auth.entities.RefreshToken;
import com.project.MovieApi.auth.entities.User;
import com.project.MovieApi.auth.services.JwtService;
import com.project.MovieApi.auth.services.RefreshTokenService;
import com.project.MovieApi.auth.utils.AuthResponse;
import com.project.MovieApi.auth.utils.LoginRequest;
import com.project.MovieApi.auth.utils.RefreshTokenRequest;
import com.project.MovieApi.auth.utils.RegisterRequest;
import com.project.MovieApi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController
{

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest)
    {
        System.out.println("Enterd the register Controller");
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest)
    {
        return ResponseEntity.ok(authService.login(loginRequest));
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {

        System.out.println("Start of AuthController");
        System.out.println(refreshTokenRequest.toString());
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshTokenRequest());
        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(user);


        System.out.println("End or before return of AuthController");
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build());
    }
}
