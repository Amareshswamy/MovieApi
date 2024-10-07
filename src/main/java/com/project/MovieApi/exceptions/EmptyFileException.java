package com.project.MovieApi.exceptions;

public class EmptyFileException extends RuntimeException
{
    public EmptyFileException(String message)
    {
        super(message);
    }

}
