package com.varkalys.homework1.protocol.request;

import com.varkalys.homework1.protocol.Message;
import com.varkalys.homework1.protocol.util.SerializeUtil;

public class GuessRequest implements Message {

    private String guess;

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }

    @Override
    public byte[] serialize() {
        return SerializeUtil.serializeAttributes(guess);
    }

    @Override
    public void deserialize(byte[] messageBytes) {
        String[] attributes = SerializeUtil.deserializeAttributes(messageBytes);
        guess = attributes[0];
    }
}
