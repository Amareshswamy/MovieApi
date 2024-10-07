package com.project.MovieApi.auth.services;

import com.project.MovieApi.auth.entities.RefreshToken;
import com.project.MovieApi.auth.entities.User;
import com.project.MovieApi.auth.repositories.RefreshTokenRepository;
import com.project.MovieApi.auth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService
{
    @Autowired
    private UserRepository userRepository;


    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }


    public RefreshToken createRefreshToken(String username)
    {
       User user =  userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

       RefreshToken refreshToken =  user.getRefreshToken();

       if(refreshToken==null)
       {
           long refreshTokenValidity = 5*60*60*10000;
           refreshToken = RefreshToken.builder()
                   .refreshToken(UUID.randomUUID().toString())
                   .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                   .user(user)
                   .build();


           refreshTokenRepository.save(refreshToken);
       }

       return  refreshToken;

    }

    public RefreshToken verifyRefreshToken(String refreshToken)
    {
        System.out.println("Start of Refresh token serive");
       /* if (refreshToken == null || refreshToken.isEmpty()) {
            System.out.println("Refresh token is null or empty");
            throw new IllegalArgumentException("Refresh token is missing or invalid");
        }*/

        RefreshToken refToken =  refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("varify part Refresh token not found"+ refreshToken));

        if(refToken.getExpirationTime().compareTo(Instant.now() )< 0)
        {
            refreshTokenRepository.delete(refToken);
             throw new RuntimeException("Generate new Exception");
        }
        System.out.println("End of Refresh token serive");
        return refToken;
    }
}
