package com.example.newsapp.client;


import com.example.newsapp.model.dto.NewsArticleDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class GNewsApiClient implements NewsApiClient {

    private final WebClient webClient;
    private final String apiKey;

    public GNewsApiClient(WebClient webClient, @Value("${gnews.key}") String apiKey) {
        this.webClient = webClient;
        this.apiKey = apiKey;
    }

    @Override
    public CompletableFuture<List<NewsArticleDTO>> fetchNews(List<String> preferences, String keyword) {
        String path;
        String query;
        if (keyword != null && !keyword.isEmpty()) {
            path = "/search";
            query = keyword;
        } else if (preferences != null && !preferences.isEmpty()) {
            path = "/search";
            query = String.join(" OR ", preferences);
        } else {
            path = "/top-headlines";
            query = "";
        }

        Mono<List<NewsArticleDTO>> mono = webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder = uriBuilder.scheme("https")
                            .host("gnews.io")
                            .path("/api/v4" + path)
                            .queryParam("token", apiKey)
                            .queryParam("lang", "en");
                    if (!query.isEmpty()) {
                        uriBuilder = uriBuilder.queryParam("q", URLEncoder.encode(query, StandardCharsets.UTF_8));
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    List<NewsArticleDTO> list = new ArrayList<>();
                    JsonNode articlesNode = json.path("articles");
                    if (articlesNode.isArray()) {
                        for (JsonNode article : articlesNode) {
                            String id = article.path("url").asText("");
                            String title = article.path("title").asText("");
                            String description = article.path("description").asText("");
                            String urlStr = article.path("url").asText("");
                            String source = article.path("source").path("name").asText("");
                            String publishedAt = article.path("publishedAt").asText("");
                            list.add(new NewsArticleDTO(id, title, description, urlStr, source, publishedAt));
                        }
                    }
                    return list;
                });

        return mono.toFuture();
    }
}