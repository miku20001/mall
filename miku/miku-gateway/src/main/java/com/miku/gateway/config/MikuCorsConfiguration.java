package com.miku.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class MikuCorsConfiguration {

    @Bean
    public CorsFilter corsFilter(){

        //初始化配置对象
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://manage.miku.com");
        configuration.addAllowedOrigin("http://www.miku.com");
        configuration.setAllowCredentials(true);
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");

        //初始化配置源对象
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",configuration);

        return new CorsFilter(configurationSource);
    }
}
