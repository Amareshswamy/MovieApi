package com.project.MovieApi.controller;

import com.project.MovieApi.auth.repositories.UserRepository;
import com.project.MovieApi.auth.utils.ChangePassword;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/reset")
public class ResetController
{
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    public ResetController(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/resetPassword/{email}")
    public ResponseEntity<String> resetPasswordHandler(@RequestBody ChangePassword changePassword, @PathVariable String email)
    {
        if(!Objects.equals(changePassword.password(), changePassword.repeatPassword()))
        {
            return new ResponseEntity<>("The Password does not match, Please enter the valid passwprd", HttpStatus.EXPECTATION_FAILED);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());

        userRepository.updatePassword(email,encodedPassword);
        return ResponseEntity.ok("Password change successful !!");
    }

}
