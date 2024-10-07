package com.project.MovieApi.auth.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Data
public class ForgotPassword
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fpid;

    private Integer otp;

    private Date expirationTime;


    @OneToOne
    private User user;

}
