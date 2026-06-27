package ding.co.hellojpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HelloJpaApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelloJpaApplication.class, args);
    }
}
