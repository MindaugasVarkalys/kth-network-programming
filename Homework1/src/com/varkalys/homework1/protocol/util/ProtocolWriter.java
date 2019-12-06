package com.varkalys.homework1.protocol.util;

import com.varkalys.homework1.protocol.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.nio.ByteBuffer;

public final class ProtocolWriter {

    public static void writeMessage(OutputStream os, Message message) throws IOException {
        byte[] messageBytes = message.serialize();
        writeLength(os, messageBytes.length);
        os.write(messageBytes);
    }

    private static void writeLength(OutputStream os, int length) throws IOException {
        byte[] lengthBytes = ByteBuffer.allocate(4).putInt(length).array();
        os.write(lengthBytes);
    }
}
