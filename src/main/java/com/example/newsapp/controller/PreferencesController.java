package com.example.newsapp.controller;

import com.example.newsapp.model.User;
import com.example.newsapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preferences")
public class PreferencesController {

    private final UserService userService;

    public PreferencesController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getPreferences(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(user.getPreferences());
    }

    @PutMapping
    public ResponseEntity<List<String>> updatePreferences(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody List<String> preferences) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        user = userService.updatePreferences(user.getId(), preferences);
        return ResponseEntity.ok(user.getPreferences());
    }
} 