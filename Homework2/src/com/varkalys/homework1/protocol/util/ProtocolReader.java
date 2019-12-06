package com.varkalys.homework1.protocol.util;

import com.varkalys.homework1.protocol.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public final class ProtocolReader {

    public static <T extends Message> T readMessage(SocketChannel sc, T message) throws IOException {
        byte[] messageBytes = readMessageBytes(sc);
        message.deserialize(messageBytes);
        return message;
    }

    private static byte[] readMessageBytes(SocketChannel sc) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        sc.read(buffer);
        return buffer.array();
    }
}
