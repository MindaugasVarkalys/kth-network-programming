package com.varkalys.homework1.server.controller;

import com.varkalys.homework1.protocol.request.LoginRequest;
import com.varkalys.homework1.protocol.response.LoginResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class LoginController {

    private final static String USERNAME = "admin";
    private final static String PASSWORD = "admin";

    private final static Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private String jwt = "";

    public void login(LoginRequest request, LoginResponse response) {
        if (request.getUsername().equals(USERNAME) && request.getPassword().equals(PASSWORD)) {
            jwt = generateJwt();
        }
        response.setJwt(jwt);
    }

    private String generateJwt() {
        return Jwts.builder().setSubject("Hangman").signWith(key).compact();
    }

    public boolean isLoggedIn() {
        return !this.jwt.isEmpty();
    }

    public boolean isTokenValid(String jwt) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody().getSubject().equals("Hangman");
    }
}
