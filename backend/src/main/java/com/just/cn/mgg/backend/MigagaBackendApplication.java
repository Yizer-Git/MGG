package com.just.cn.mgg.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MigagaBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(MigagaBackendApplication.class, args);
    }
}
