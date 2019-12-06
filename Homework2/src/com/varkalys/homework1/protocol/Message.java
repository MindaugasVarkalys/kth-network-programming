package com.varkalys.homework1.protocol;

public interface Message {

    byte[] serialize();
    void deserialize(byte[] messageBytes);
}
