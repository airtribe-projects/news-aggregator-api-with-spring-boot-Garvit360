package com.example.newsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.example.newsapp.config.JwtProperties;
import com.example.newsapp.config.GNewsProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, GNewsProperties.class})
public class NewsApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewsApplication.class, args);
    }
} 