package com.example.newsapp.client;

import com.example.newsapp.model.dto.NewsArticleDTO;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Primary
public class MockNewsApiClient implements NewsApiClient {
    @Override
    public CompletableFuture<List<NewsArticleDTO>> fetchNews(List<String> preferences, String keyword) {
        // Return mock data for testing
        List<NewsArticleDTO> mockArticles = Arrays.asList(
                new NewsArticleDTO("1", "Tech News: AI Revolution", "Artificial intelligence is transforming the world",
                        "https://example.com/tech-news-1", "https://example.com/tech-image-1.jpg", "2024-01-01"),
                new NewsArticleDTO("2", "Science Discovery: New Planet Found", "Scientists discover a new exoplanet",
                        "https://example.com/science-news-1", "https://example.com/science-image-1.jpg", "2024-01-02"),
                new NewsArticleDTO("3", "Business Update: Market Growth", "Stock market shows positive growth",
                        "https://example.com/business-news-1", "https://example.com/business-image-1.jpg",
                        "2024-01-03"));
        return CompletableFuture.completedFuture(mockArticles);
    }
}