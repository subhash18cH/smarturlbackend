package com.subhash.smrturlf.request;

import lombok.Data;

@Data
public class LoginRequest {

    private String userName;

    private String password;

    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
