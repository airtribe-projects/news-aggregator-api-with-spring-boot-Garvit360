package com.example.newsapp.client;

import com.example.newsapp.model.dto.NewsArticleDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NewsApiClient {
    CompletableFuture<List<NewsArticleDTO>> fetchNews(List<String> preferences, String keyword);
} 