package com.example.newsapp.client;

import com.example.newsapp.model.dto.NewsArticleDTO;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Primary
public class MockNewsApiClient implements NewsApiClient {
    @Override
    public CompletableFuture<List<NewsArticleDTO>> fetchNews(List<String> preferences, String keyword) {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }
} 