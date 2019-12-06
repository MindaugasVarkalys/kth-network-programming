package com.varkalys.homework1.client;

import com.varkalys.homework1.protocol.request.GuessRequest;
import com.varkalys.homework1.protocol.response.GuessResponse;

import java.io.IOException;
import java.util.Scanner;

public class Hangman {

    private Client client;
    private final Scanner in = new Scanner(System.in);

    public void run() throws IOException {
        connect();
        guess(" ");
    }

    private void connect() throws IOException {
        client = new Client();
        client.connect();
    }

    private void guess(String guess) {
        GuessRequest request = new GuessRequest();
        request.setGuess(guess);
        client.guess(request, result -> {
            GuessResponse response = (GuessResponse) result;
            System.out.println("--------------------------------");
            System.out.println("Score: " + response.getScore());
            System.out.println("Remaining attempts: " + response.getRemainingAttempts());
            System.out.println("Word: " + response.getGuessedLetters());
            guess(in.nextLine());
        });
    }
}
