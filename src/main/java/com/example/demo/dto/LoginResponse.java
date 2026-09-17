package com.example.demo.dto;

public class LoginResponse {

    private Integer id;
    private String email;
    private String name;
    private String address;

    private String accessToken;
    private String refreshToken;

    public LoginResponse() {
    }

    public LoginResponse(
            Integer id,
            String email,
            String name,
            String address,
            String accessToken,
            String refreshToken) {

        this.id = id;
        this.email = email;
        this.name = name;
        this.address = address;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}