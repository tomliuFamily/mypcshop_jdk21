package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeployTestController {

    @GetMapping("/api/deploy-test")
    public String deployTest() {
        return "Jenkins CI/CD Version 5";
    }
}