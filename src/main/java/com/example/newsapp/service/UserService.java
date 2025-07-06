package com.example.newsapp.service;

import com.example.newsapp.model.User;
import com.example.newsapp.model.dto.UserDTO;
import com.example.newsapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username already in use");
        }
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPreferences(new ArrayList<>());
        user.setReadArticles(new ArrayList<>());
        user.setFavoriteArticles(new ArrayList<>());
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<String> getPreferences(Long userId) {
        return userRepository.findById(userId)
                .map(User::getPreferences)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updatePreferences(Long userId, List<String> preferences) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPreferences(preferences);
        return userRepository.save(user);
    }

    public User markArticleAsRead(Long userId, String articleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getReadArticles() == null) {
            user.setReadArticles(new ArrayList<>());
        }

        if (!user.getReadArticles().contains(articleId)) {
            user.getReadArticles().add(articleId);
        }

        return userRepository.save(user);
    }

    public User markArticleAsFavorite(Long userId, String articleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getFavoriteArticles() == null) {
            user.setFavoriteArticles(new ArrayList<>());
        }

        if (!user.getFavoriteArticles().contains(articleId)) {
            user.getFavoriteArticles().add(articleId);
        }

        return userRepository.save(user);
    }

    public List<String> getReadArticles(Long userId) {
        return userRepository.findById(userId)
                .map(User::getReadArticles)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<String> getFavoriteArticles(Long userId) {
        return userRepository.findById(userId)
                .map(User::getFavoriteArticles)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}