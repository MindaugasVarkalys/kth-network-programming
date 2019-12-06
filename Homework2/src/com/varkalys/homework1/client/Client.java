package com.varkalys.homework1.client;

import com.varkalys.homework1.protocol.Future;
import com.varkalys.homework1.protocol.Message;
import com.varkalys.homework1.protocol.request.GuessRequest;
import com.varkalys.homework1.protocol.response.GuessResponse;
import com.varkalys.homework1.protocol.util.ProtocolReader;
import com.varkalys.homework1.protocol.util.ProtocolWriter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Client {

    private final static String HOST = "127.0.0.1";
    private final static int PORT = 4223;

    private Selector selector;

    private Message sendingMessage = new GuessRequest();
    private Future<Message> receiveCallback;

    public void connect() throws IOException {
        InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName(HOST), PORT);
        selector = Selector.open();
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(addr);
        sc.register(selector, SelectionKey.OP_CONNECT);
    }

    public void guess(GuessRequest guessRequest, Future<Message> receiveCallback) {
        sendingMessage = guessRequest;
        this.receiveCallback = receiveCallback;
        readWriteAsync();
    }

    private void readWriteAsync() {
        new Thread(() -> {
            try {
                readWrite();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void readWrite() throws IOException {
        while (true) {
            selector.select();
            for (SelectionKey key : selector.selectedKeys()) {
                SocketChannel sc = (SocketChannel) key.channel();
                if (key.isConnectable()) {
                    while (sc.isConnectionPending()) {
                        sc.finishConnect();
                    }
                    key.interestOps(SelectionKey.OP_WRITE);
                }
                if (key.isWritable()) {
                    ProtocolWriter.writeMessage(sc, sendingMessage);
                    key.interestOps(SelectionKey.OP_READ);
                }
                if (key.isReadable()) {
                    GuessResponse response = ProtocolReader.readMessage(sc, new GuessResponse());
                    new Thread(() -> {
                        try {
                            receiveCallback.onResult(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    key.interestOps(SelectionKey.OP_WRITE);
                    return;
                }
            }
        }
    }
}
