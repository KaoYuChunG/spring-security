package com.kao.yu.securityconcurrentsession.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
