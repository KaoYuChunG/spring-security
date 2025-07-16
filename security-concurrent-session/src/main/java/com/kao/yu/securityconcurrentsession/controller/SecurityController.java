package com.kao.yu.securityconcurrentsession.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SecurityController {

    @GetMapping("/public")
    public String publicHello() {
        return "Hello, public!";
    }

    @GetMapping("/user")
    public String userHello() {
        return "Hello, user!";
    }

    @GetMapping("/admin")
    public String adminHello() {
        return "Hello, admin!";
    }
}
