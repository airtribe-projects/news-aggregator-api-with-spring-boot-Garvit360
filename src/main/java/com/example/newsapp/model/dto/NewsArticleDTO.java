package com.example.newsapp.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticleDTO {
    private String id;
    private String title;
    private String description;
    private String url;
    private String source;
    private String publishedAt;
} 