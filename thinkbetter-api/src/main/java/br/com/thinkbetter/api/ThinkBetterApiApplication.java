package br.com.thinkbetter.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ThinkBetterApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThinkBetterApiApplication.class, args);
    }

    /** Gera e confere hashes de senha (as senhas nunca são gravadas em texto). */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
