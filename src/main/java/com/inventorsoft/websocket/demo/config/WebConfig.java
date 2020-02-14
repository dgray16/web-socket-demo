package com.inventorsoft.websocket.demo.config;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }

    @Bean
    ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

        threadPoolTaskExecutor.setThreadNamePrefix("custom-pool-");
        threadPoolTaskExecutor.setCorePoolSize(NumberUtils.INTEGER_TWO);
        threadPoolTaskExecutor.setMaxPoolSize(NumberUtils.INTEGER_TWO);

        return threadPoolTaskExecutor;
    }

}
