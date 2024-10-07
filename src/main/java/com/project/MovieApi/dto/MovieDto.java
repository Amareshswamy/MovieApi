package com.project.MovieApi.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MovieDto
{


    private Integer movieId;


    @NotBlank(message = "Please enter the movie title")
    private String title;


    @NotBlank(message = "Please enter the movie Director")
    private String director;


    @NotBlank(message = "Please enter the movie Studio")
    private String studio;


    private Set<String> movieCast;


    private Integer releaseYear;


    @NotBlank(message = "Please enter the movie Poster")
    private String poster;

    @NotBlank(message = "please provide poster's url")
    private String posterUrl;
}
