package com.zhuhongyu.pacific.config;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class SecondDbConfig {

    @Value("${se.db.host}")
    private String host;
    @Value("${se.db.port}")
    private int port;

    @Bean
    public MongoTemplate seMongoTemplate() {
        return new MongoTemplate(new MongoClient(host, port), "pacific");
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(new MongoClient(host, port), "mars");
    }
}
