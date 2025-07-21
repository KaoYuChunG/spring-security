package com.kao.yu.statelessauthentication.config;


import com.kao.yu.statelessauthentication.dto.Users;
import com.kao.yu.statelessauthentication.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new Users(null, "admin", encoder.encode("admin123"), List.of("ADMIN", "USER")));
                repo.save(new Users(null, "user", encoder.encode("user123"), List.of("USER")));
            }
        };
    }
}
