package com.varkalys.homework1.protocol.util;

import com.varkalys.homework1.protocol.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.nio.ByteBuffer;

public final class ProtocolReader {

    public static <T extends Message> T readMessage(InputStream is, T message) throws IOException {
        int length = readLength(is);
        byte[] messageBytes = readMessageBytes(is, length);
        message.deserialize(messageBytes);
        return message;
    }

    private static int readLength(InputStream is) throws IOException {
        byte[] buffer = new byte[4];
        int read = is.read(buffer);
        if (read != 4) {
            throw new ProtocolException("Length of the message is not provided");
        }
        return ByteBuffer.wrap(buffer).getInt();
    }

    private static byte[] readMessageBytes(InputStream is, int length) throws IOException {
        byte[] buffer = new byte[length];
        int read = 0;
        while (read < length) {
            read += is.read(buffer, read, length - read);
        }
        return buffer;
    }
}
