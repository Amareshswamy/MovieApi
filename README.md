# MovieApi

MovieApi is a RESTful web application built using the Spring Boot framework and Java 21. It provides features like user login, registration, admin login, password reset, and forget password functionality. The API allows for managing movie records with support for pagination and sorting, and handles CRUD operations.

## Tech Stack

- **Java 21**
- **Spring Boot**
- **File Handling**
- **Exception Handling**
- **Pagination & Sorting**
- **JWT Authentication**
- **Lombok**
- **Postman** (for API testing)

## Features

- **User Authentication**: Login, registration, and JWT-based authentication.
- **Password Management**: Reset password and forget password functionalities.
- **Movie Management**: Add, update, delete, and fetch movie records.
- **Admin Functionality**: Admin login and movie management.

## API Endpoints

### Authentication

- **Login**  
  `POST /api/v1/auth/login`
  
- **Refresh JWT Token**  
  `POST /api/v1/auth/refresh`
  
- **User Registration**  
  `POST /api/v1/auth/register`

### Password Recovery

- **Change Password**  
  `POST /forgotPassword/changePassword/{email}`

- **Verify OTP**  
  `GET /forgotPassword/verify/{otp}/{email}`

- **Verify Email for Password Recovery**  
  `GET /forgotPassword/verifyMail/{email}`

- **Reset Password**  
  `POST /reset/resetPassword/{email}`

### Movie Management

- **Add a Movie**  
  `POST /api/v1/movie/add-movie`
  
- **Get All Movies**  
  `GET /api/v1/movie/all`
  
- **Delete a Movie by ID**  
  `DELETE /api/v1/movie/delete/{movieId}`

- **Update a Movie by ID**  
  `PUT /api/v1/movie/update/{movieId}`

- **Get a Movie by ID**  
  `GET /api/v1/movie/{movieId}`
