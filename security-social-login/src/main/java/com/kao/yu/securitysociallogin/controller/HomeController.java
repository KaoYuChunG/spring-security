package com.kao.yu.securitysociallogin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class HomeController {

    @GetMapping("/api/userinfo")
    public ResponseEntity<String> userInfo(Principal principal) {
        return ResponseEntity.ok("Hello, " + principal.getName());
    }
}
