package com.varkalys.homework1.protocol.util;

import com.varkalys.homework1.protocol.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public final class ProtocolWriter {

    public static void writeMessage(SocketChannel sc, Message message) throws IOException {
        byte[] messageBytes = message.serialize();
        sc.write(ByteBuffer.wrap(messageBytes));
    }
}
