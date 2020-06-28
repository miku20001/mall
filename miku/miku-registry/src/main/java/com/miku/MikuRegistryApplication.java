package com.miku;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MikuRegistryApplication {
    public static void main(String[] args) {
        SpringApplication.run(MikuRegistryApplication.class,args);
    }
}
