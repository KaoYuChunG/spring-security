package com.kao.yu.securitymenory.config;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SecurityConfig.class)
public class SecurityConfigTest {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private  PasswordEncoder passwordEncoder;


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
        assertTrue(passwordEncoder.matches("123456", user.getPassword()));
        assertTrue(user.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER")));
    }
}
