package com.example.newsapp.service;

import com.example.newsapp.client.NewsApiClient;
import com.example.newsapp.model.dto.NewsArticleDTO;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private final List<NewsApiClient> apiClients;

    public NewsService(List<NewsApiClient> apiClients) {
        this.apiClients = apiClients;
    }

    @Cacheable(value = "news", key = "#preferences != null ? #preferences.toString() : 'all'")
    public List<NewsArticleDTO> getNews(List<String> preferences) {
        List<CompletableFuture<List<NewsArticleDTO>>> futures = apiClients.stream()
                .map(client -> client.fetchNews(preferences, null))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "news", key = "#keyword")
    public List<NewsArticleDTO> searchNews(String keyword) {
        List<CompletableFuture<List<NewsArticleDTO>>> futures = apiClients.stream()
                .map(client -> client.fetchNews(null, keyword))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
} 