package com.project.MovieApi.controller;

import com.project.MovieApi.auth.entities.ForgotPassword;
import com.project.MovieApi.auth.entities.User;
import com.project.MovieApi.auth.repositories.ForgotPasswordRepository;
import com.project.MovieApi.auth.repositories.UserRepository;
import com.project.MovieApi.auth.utils.ChangePassword;
import com.project.MovieApi.dto.MailBody;
import com.project.MovieApi.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(UserRepository userRepository, ForgotPasswordRepository forgotPasswordRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }


    //send mail for email verification

    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email)
    {
        System.out.println("Start of verify mail");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("please provide valid email"));

        System.out.println("otp generation start");
        int otp = optGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for forget password request:" + otp)
                .subject("OTP for forget passowrd")
                .build();

        System.out.println("otp generation END");


       // int expiryTime = (int) (System.currentTimeMillis() / 1000 + 70);  // Current time in seconds + 70 seconds

        System.out.println("forget password builder start");
        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 70 * 1000))
                .user(user)
                .build();
        System.out.println("forget password builder END");

        System.out.println("Email service start");
        emailService.sendSimpleMessage(mailBody);
        System.out.println("After email service");
                forgotPasswordRepository.save(fp);


        System.out.println("End of verify mail");
                return ResponseEntity.ok("Email sent for verification");

    }


    @PostMapping("/verify/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException("please provide valid email"));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new RuntimeException("Please provide valid Email: " + email));

        if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(fp.getFpid());
            return new ResponseEntity<>("OTP is Expired !", HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok("OTP verified !");

    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword, @PathVariable String email)
    {
        if(!Objects.equals(changePassword.password(), changePassword.repeatPassword()))
        {
            return new ResponseEntity<>("The Password does not match, Please enter the valid passwprd",HttpStatus.EXPECTATION_FAILED);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());

        userRepository.updatePassword(email,encodedPassword);
        return ResponseEntity.ok("Password change successful !!");
    }




    private Integer optGenerator()
    {
        Random random = new Random();
        return random.nextInt(100_000,999_999);
    }
}
