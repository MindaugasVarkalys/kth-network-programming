package com.varkalys.homework1.server.controller;

import com.varkalys.homework1.protocol.request.GuessRequest;
import com.varkalys.homework1.protocol.response.GuessResponse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GuessController {

    private final ArrayList<String> words = new ArrayList<>();
    private final Random random = new Random();

    private String word;
    private StringBuilder foundLetters;
    private int score;
    private int leftAttempts;

    public GuessController() {
        readWords();
    }

    private void readWords() {
        new Thread(() -> {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader("C:\\Users\\minda\\Programs\\KTH\\Network Programming\\Homework1\\words.txt"));
                String line = reader.readLine();
                while (line != null) {
                    words.add(line);
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void guess(GuessRequest request, GuessResponse response) {
        if (word == null || leftAttempts == 0 || word.contentEquals(foundLetters)) {
            selectWord();
            leftAttempts = word.length();
        } else if (request.getGuess().length() > 0) {
            boolean found;
            String guess = request.getGuess();
            // Guessing single letter
            if (guess.length() == 1) {
                found = replaceGuessedLetters(guess.charAt(0));
            } else {
                found = word.equals(guess);
                if (found) {
                    foundLetters = new StringBuilder(word);
                }
            }

            if (!found) {
                leftAttempts--;
                if (leftAttempts == 0) {
                    score--;
                    foundLetters = new StringBuilder(word);
                }
            } else if (foundLetters.indexOf("-") == -1) {
                score++;
            }
        }

        response.setGuessedLetters(foundLetters.toString());
        response.setRemainingAttempts(leftAttempts);
        response.setScore(score);
    }

    private void selectWord() {
        word = words.get(random.nextInt(words.size()));

        foundLetters = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            foundLetters.append("-");
        }
    }

    private boolean replaceGuessedLetters(char guessedLetter) {
        boolean found = false;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == guessedLetter) {
                foundLetters.setCharAt(i, guessedLetter);
                found = true;
            }
        }
        return found;
    }
}
