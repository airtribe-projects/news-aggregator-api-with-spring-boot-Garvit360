package com.example.newsapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> preferences;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> readArticles;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> favoriteArticles;
}