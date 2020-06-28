package com.miku;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MikuUploadApplication {
    public static void main(String[] args) {
        SpringApplication.run(MikuUploadApplication.class);
    }
}
