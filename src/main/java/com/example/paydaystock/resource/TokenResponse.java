package com.example.paydaystock.resource;

import java.util.List;

public class TokenResponse {
    private String token;
    private String type = "Bearer";
    private Integer id;
    private String email;
    private List<String> authority;

    public TokenResponse(String accessToken, Integer id, String email, List<String> authority) {
        this.token = accessToken;
        this.id = id;
        this.email = email;
        this.authority = authority;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
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

    public List<String> getAuthority() {
        return authority;
    }
}
