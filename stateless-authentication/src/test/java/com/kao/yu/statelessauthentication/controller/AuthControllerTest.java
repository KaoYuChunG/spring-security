package com.kao.yu.statelessauthentication.controller;


import com.kao.yu.statelessauthentication.config.jwt.JwtUtil;
import com.kao.yu.statelessauthentication.dto.AuthRequest;
import com.kao.yu.statelessauthentication.dto.TokenRefreshRequest;
import com.kao.yu.statelessauthentication.dto.TokenRefreshResponse;
import com.kao.yu.statelessauthentication.dto.Users;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {


    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Test
    void testLoginSuccess() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("admin");
        authRequest.setPassword("admin123");

        UserDetails userDetails = getUserDetails(authRequest);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateAccessToken(userDetails)).thenReturn("mockedAccessToken");
        when(jwtUtil.generateRefreshToken(userDetails)).thenReturn("mockedRefreshToken");

        ResponseEntity<?> response = authController.login(authRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> tokens = (Map<String, String>) response.getBody();

        assertEquals("mockedAccessToken", tokens.get("accessToken"));
        assertEquals("mockedRefreshToken", tokens.get("refreshToken"));
    }

    @Test
    void testRefreshTokenSuccess() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("admin");
        authRequest.setPassword("admin123");

        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken("validRefreshToken");

        when(jwtUtil.isRefreshTokenValid("validRefreshToken")).thenReturn(true);
        when(jwtUtil.extractUsernameFromRefreshToken("validRefreshToken")).thenReturn("admin");

        UserDetails userDetails = getUserDetails(authRequest);

        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtUtil.generateAccessToken(userDetails)).thenReturn("newAccessToken");

        ResponseEntity<?> response = authController.refreshToken(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        TokenRefreshResponse body = (TokenRefreshResponse) response.getBody();

        assertEquals("newAccessToken", body.getAccessToken());
        assertEquals("validRefreshToken", body.getRefreshToken());
    }

    private UserDetails getUserDetails(AuthRequest authRequest){
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }

            @Override
            public String getPassword() {
                return authRequest.getPassword();
            }

            @Override
            public String getUsername() {
                return authRequest.getUsername();
            }
        };
    }
}
