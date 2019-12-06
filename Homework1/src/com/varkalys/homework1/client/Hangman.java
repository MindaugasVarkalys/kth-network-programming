package com.varkalys.homework1.client;

import com.varkalys.homework1.protocol.request.GuessRequest;
import com.varkalys.homework1.protocol.request.LoginRequest;
import com.varkalys.homework1.protocol.response.GuessResponse;
import com.varkalys.homework1.protocol.response.LoginResponse;

import java.io.IOException;
import java.util.Scanner;

public class Hangman {

    private Client client;
    private final Scanner in = new Scanner(System.in);
    private String jwt;

    public void start() {
        new Thread(() -> {
            try {
                run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void run() throws IOException {
        connect();
        login();
        guess();
    }

    private void connect() throws IOException {
        System.out.println("Connecting...");
        client = new Client();
        client.connect();
        System.out.println("Connected.");
    }

    private void login() throws IOException {
        LoginRequest loginRequest = new LoginRequest();
        System.out.print("Username: ");
        loginRequest.setUsername(in.nextLine());
        System.out.print("Password: ");
        loginRequest.setPassword(in.nextLine());

        LoginResponse loginResponse = client.login(loginRequest);
        jwt = loginResponse.getJwt();
        if (jwt.isEmpty()) {
            System.out.println("Incorrect username or password");
            login();
        }
    }

    private void guess() throws IOException {
        GuessRequest request = new GuessRequest();
        request.setJwt(jwt);
        while (true) {
            GuessResponse response = client.guess(request);
            System.out.println("--------------------------------");
            System.out.println("Score: " + response.getScore());
            System.out.println("Remaining attempts: " + response.getRemainingAttempts());
            System.out.println("Word: " + response.getGuessedLetters());
            request.setGuess(in.nextLine());
        }
    }
}
