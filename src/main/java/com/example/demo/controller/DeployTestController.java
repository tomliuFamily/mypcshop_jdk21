package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeployTestController {

    @GetMapping("/api/deploy-test")
    public String deployTest() {
        return "Jenkins CI/CD Version 自動更新 test successful!  9/30 自動建置-1";
    }
}