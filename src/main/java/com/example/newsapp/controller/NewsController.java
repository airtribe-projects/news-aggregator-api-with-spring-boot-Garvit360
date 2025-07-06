package com.example.newsapp.controller;

import com.example.newsapp.model.User;
import com.example.newsapp.model.dto.NewsArticleDTO;
import com.example.newsapp.service.NewsService;
import com.example.newsapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;
    private final UserService userService;

    public NewsController(NewsService newsService, UserService userService) {
        this.newsService = newsService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<NewsArticleDTO>> getNews(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(newsService.getNews(user.getPreferences()));
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<NewsArticleDTO>> searchNews(@PathVariable String keyword) {
        return ResponseEntity.ok(newsService.searchNews(keyword));
    }
} 