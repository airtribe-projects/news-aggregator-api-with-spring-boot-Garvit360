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

    @PostMapping("/{id}/read")
    public ResponseEntity<String> markArticleAsRead(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        userService.markArticleAsRead(user.getId(), id);
        return ResponseEntity.ok("Article marked as read");
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<String> markArticleAsFavorite(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        userService.markArticleAsFavorite(user.getId(), id);
        return ResponseEntity.ok("Article marked as favorite");
    }

    @GetMapping("/read")
    public ResponseEntity<List<NewsArticleDTO>> getReadArticles(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        List<String> readArticleIds = userService.getReadArticles(user.getId());
        List<NewsArticleDTO> readArticles = newsService.getReadArticles(readArticleIds, user.getPreferences());
        return ResponseEntity.ok(readArticles);
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<NewsArticleDTO>> getFavoriteArticles(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        List<String> favoriteArticleIds = userService.getFavoriteArticles(user.getId());
        List<NewsArticleDTO> favoriteArticles = newsService.getFavoriteArticles(favoriteArticleIds,
                user.getPreferences());
        return ResponseEntity.ok(favoriteArticles);
    }
} 