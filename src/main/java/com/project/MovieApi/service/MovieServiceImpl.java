package com.project.MovieApi.service;

import com.project.MovieApi.dto.MovieDto;
import com.project.MovieApi.dto.MoviePageResponse;
import com.project.MovieApi.entities.Movie;
import com.project.MovieApi.exceptions.FileExistsException;
import com.project.MovieApi.exceptions.MovieNotFoundException;
import com.project.MovieApi.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService{


    private final MovieRepository movieRepository;

    private final FileService fileService;


    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;



    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        //1. to uplode the file

      if( Files.exists(Paths.get(path + File.separator + file.getOriginalFilename())))
        {
            throw new FileExistsException("Files already exists ! please enter another file name!");
        }
        String uploadedFileName = fileService.uploadFile(path, file);


        //2. to set the value of field 'poster' as filename
        movieDto.setPoster(uploadedFileName);


        //3.map dto to Movie object
        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster());


        //4. to save the movie object -> saved movie object
        Movie savedMovie = movieRepository.save(movie);

        //5. generate the posterUrl
        String posterUrl = baseUrl + "/file/" + uploadedFileName;

        //6.map Movie object to DTO object and return it
        MovieDto response = new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );

        return response;

    }

    @Override
    public MovieDto getMovie(Integer movieId)
    {
        // 1. check the data in the DB and if any exist, fetch data of given ID
      Movie movie =  movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found with id = "+  movieId ));

        //2. generate posterUrl
        String posterUrl = baseUrl + "/file/" + movie.getPoster();


        //3.map the MovieDto object and return it.
        MovieDto response = new MovieDto(
            movie.getMovieId(),
            movie.getTitle(),
            movie.getDirector(),
            movie.getStudio(),
            movie.getMovieCast(),
            movie.getReleaseYear(),
            movie.getPoster(),
            posterUrl
    );



        return response;
    }

    @Override
    public List<MovieDto> getAllMovies()
    {

        // 1 to fetch all the data from db
        List<Movie> movies = movieRepository.findAll();

        List<MovieDto> movieDtos = new ArrayList<>();

        //2 itrate through the list and generate the poster url for each mvoie ID;
        // and map to MovieDto

        for(Movie movie : movies)
        {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }
        return movieDtos;


    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException
    {
        // check if movie exist
        Movie mv =  movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found with id = "+  movieId));


        //if file is null, do nothing
        String fileName = mv.getPoster();

        //if file is not null then delete existing file associated and uplode the new file
        //and uplode the new file

        if(file!=null)
        {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path,file);
        }



        // set movieDto's poster value according to step 3
        movieDto.setPoster(fileName);

        //map it to movie object
        Movie movie = new Movie(
                mv.getMovieId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster());

        //save the movie object -> return saved movie object
        Movie updatedMovie = movieRepository.save(movie);

        //generate poster url from it.
        String posterUrl = baseUrl + "/file/" + fileName;

        //map to moviedto and return it.
        MovieDto response = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {

        //check if movie is present in db
        Movie mv =  movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found with id = "+  movieId));


        //dalate the file accociated
        Files.deleteIfExists(Paths.get(path + File.separator + mv.getPoster()));

        //delete thr object
        movieRepository.delete(mv);

        return "Movies is updated";
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        org.springframework.data.domain.Pageable pageable = PageRequest.of(pageNumber,pageSize);


       Page<Movie> moviePages = movieRepository.findAll((org.springframework.data.domain.Pageable) pageable);

        List<Movie> movies = moviePages.getContent();


        List<MovieDto> movieDtos = new ArrayList<>();

        //2 itrate through the list and generate the poster url for each mvoie ID;
        // and map to MovieDto

        for(Movie movie : movies)
        {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }

        return new MoviePageResponse(movieDtos,pageNumber,pageSize,
                (int) moviePages.getTotalElements(), moviePages.getTotalPages(),moviePages.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {

        Sort  sort = dir.equalsIgnoreCase("asc") ?  Sort.by(sortBy).ascending():Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);


        Page<Movie> moviePages = movieRepository.findAll((org.springframework.data.domain.Pageable) pageable);

        List<Movie> movies = moviePages.getContent();


        List<MovieDto> movieDtos = new ArrayList<>();

        //2 itrate through the list and generate the poster url for each mvoie ID;
        // and map to MovieDto

        for(Movie movie : movies)
        {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }

        return new MoviePageResponse(movieDtos,pageNumber,pageSize,
                moviePages.getTotalPages(), (int) moviePages.getTotalElements(),moviePages.isLast());

    }

}
