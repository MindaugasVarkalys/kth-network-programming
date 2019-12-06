package com.varkalys.homework1.protocol.response;

import com.varkalys.homework1.protocol.Message;
import com.varkalys.homework1.protocol.util.SerializeUtil;

public class GuessResponse implements Message {

    private String guessedLetters;
    private int remainingAttempts;
    private int score;

    public String getGuessedLetters() {
        return guessedLetters;
    }

    public void setGuessedLetters(String guessedLetters) {
        this.guessedLetters = guessedLetters;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public void setRemainingAttempts(int remainingAttempts) {
        this.remainingAttempts = remainingAttempts;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public byte[] serialize() {
        return SerializeUtil.serializeAttributes(guessedLetters, remainingAttempts, score);
    }

    @Override
    public void deserialize(byte[] messageBytes) {
        String[] attributes = SerializeUtil.deserializeAttributes(messageBytes);
        guessedLetters = attributes[0];
        remainingAttempts = Integer.parseInt(attributes[1]);
        score = Integer.parseInt(attributes[2]);
    }
}
