package com.budongsan.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.budongsan")  // core 패키지까지 컴포넌트 스캔
@EntityScan(basePackages = "com.budongsan.core.domain")     // core의 엔티티 스캔
@EnableJpaRepositories(basePackages = "com.budongsan.api")  // api의 Repository 스캔
public class BudongsanApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudongsanApiApplication.class, args);
    }
}
