package com.example.newsapp.service;

import com.example.newsapp.client.NewsApiClient;
import com.example.newsapp.model.dto.NewsArticleDTO;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
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
                                .map(client -> client.fetchNews(preferences, null)
                                                .exceptionally(ex -> java.util.Collections.emptyList()))
                                .collect(Collectors.toList());

                return futures.stream()
                                .map(CompletableFuture::join)
                                .flatMap(List::stream)
                                .collect(Collectors.toList());
        }

            @Cacheable(value = "news", key = "#keyword")
    public List<NewsArticleDTO> searchNews(String keyword) {
        List<CompletableFuture<List<NewsArticleDTO>>> futures = apiClients.stream()
                .map(client -> client.fetchNews(null, keyword)
                        .exceptionally(ex -> java.util.Collections.emptyList()))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<NewsArticleDTO> getReadArticles(List<String> readArticleIds, List<String> preferences) {
        if (readArticleIds == null || readArticleIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Get all news articles and filter by read article IDs
        List<NewsArticleDTO> allArticles = getNews(preferences);
        return allArticles.stream()
                .filter(article -> readArticleIds.contains(article.getId()))
                .collect(Collectors.toList());
    }

    public List<NewsArticleDTO> getFavoriteArticles(List<String> favoriteArticleIds, List<String> preferences) {
        if (favoriteArticleIds == null || favoriteArticleIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Get all news articles and filter by favorite article IDs
        List<NewsArticleDTO> allArticles = getNews(preferences);
        return allArticles.stream()
                .filter(article -> favoriteArticleIds.contains(article.getId()))
                .collect(Collectors.toList());
    }
}