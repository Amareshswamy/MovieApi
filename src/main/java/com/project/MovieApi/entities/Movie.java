package com.project.MovieApi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Movie
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movieId;


    @Column(nullable = false, length = 200)
    @NotBlank(message = "Please enter the movie title")
    private String title;


    @Column(nullable = false)
    @NotBlank(message = "Please enter the movie Director")
    private String director;


    @Column(nullable = false)
    @NotBlank(message = "Please enter the movie Studio")
    private String studio;


    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> movieCast;


    @Column(nullable = false)
    private Integer releaseYear;


  /*  @Column(nullable = false)
    @NotBlank(message = "Please enter the movie Poster")*/

    private String poster;
}
