package com.varkalys.homework1.protocol.request;

import com.varkalys.homework1.protocol.Message;
import com.varkalys.homework1.protocol.util.SerializeUtil;

public class GuessRequest implements Message {

    private String jwt;
    private String guess;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }

    @Override
    public byte[] serialize() {
        return SerializeUtil.serializeAttributes(jwt, guess);
    }

    @Override
    public void deserialize(byte[] messageBytes) {
        String[] attributes = SerializeUtil.deserializeAttributes(messageBytes);
        jwt = attributes[0];
        guess = attributes.length > 1 ? attributes[1] : "";
    }
}
