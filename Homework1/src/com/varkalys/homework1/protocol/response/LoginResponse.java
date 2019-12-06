package com.varkalys.homework1.protocol.response;

import com.varkalys.homework1.protocol.Message;
import com.varkalys.homework1.protocol.util.SerializeUtil;

public class LoginResponse implements Message {

    private String jwt;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    @Override
    public byte[] serialize() {
        return SerializeUtil.serializeAttributes(jwt);
    }

    @Override
    public void deserialize(byte[] messageBytes) {
        String[] attributes = SerializeUtil.deserializeAttributes(messageBytes);
        jwt = attributes[0];
    }
}
