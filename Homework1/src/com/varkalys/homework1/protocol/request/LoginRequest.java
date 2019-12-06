package com.varkalys.homework1.protocol.request;

import com.varkalys.homework1.protocol.Message;
import com.varkalys.homework1.protocol.util.SerializeUtil;

public class LoginRequest implements Message {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public byte[] serialize() {
        return SerializeUtil.serializeAttributes(username, password);
    }

    @Override
    public void deserialize(byte[] messageBytes) {
        String[] attributes = SerializeUtil.deserializeAttributes(messageBytes);
        username = attributes[0];
        password = attributes[1];
    }
}
