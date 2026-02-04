package com.example.demo.tracing.bh;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlackHoleConfig {
    @Bean
    public BlackHole blackHole() {
        return new BlackHoleImpl();
    }
}
