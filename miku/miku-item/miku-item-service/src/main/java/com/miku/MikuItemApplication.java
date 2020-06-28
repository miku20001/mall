package com.miku;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.miku.item.mapper")
public class MikuItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(MikuItemApplication.class,args);
    }
}
