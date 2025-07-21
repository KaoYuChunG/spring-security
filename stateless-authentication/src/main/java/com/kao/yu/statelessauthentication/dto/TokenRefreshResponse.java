package com.kao.yu.statelessauthentication.dto;

import lombok.Data;

@Data
public class TokenRefreshResponse {
    private String accessToken;
    private String refreshToken;
}
