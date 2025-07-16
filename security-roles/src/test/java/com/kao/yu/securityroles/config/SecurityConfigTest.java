package com.kao.yu.securityroles.config;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {


    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    void passwordEncoder_ShouldBeBCrypt() {
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
        String rawPassword = "123456";
        String encoded = passwordEncoder.encode(rawPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encoded));
    }

    @Test
    void userDetailsService_ShouldReturnCorrectUser() {
        UserDetails user = userDetailsService.loadUserByUsername("user");
        assertEquals("user", user.getUsername());
        assertTrue(passwordEncoder.matches("user123", user.getPassword()));
        assertTrue(user.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void userDetailsService_ShouldReturnCorrectAdmin() {
        UserDetails admin = userDetailsService.loadUserByUsername("admin");
        assertEquals("admin", admin.getUsername());
        assertTrue(passwordEncoder.matches("admin123", admin.getPassword()));
        assertTrue(admin.getAuthorities().stream()
                .anyMatch(auth -> auth
                        .getAuthority().equals("ROLE_ADMIN")));
        assertTrue(admin.getAuthorities().stream()
                .anyMatch(auth -> auth
                        .getAuthority().equals("ROLE_USER")));
    }
}
