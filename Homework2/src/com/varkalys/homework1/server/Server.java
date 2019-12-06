package com.varkalys.homework1.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server {

    private final static int PORT = 4223;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public void start() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(PORT));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        startAsyncLoop();
    }

    private void startAsyncLoop() {
        new Thread(() -> {
            try {
                loop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loop() throws IOException {
        while (true) {
            selector.select();
            for (SelectionKey key : selector.selectedKeys()) {
                if (key.isReadable()) {
                    readWrite(key);
                }
                if (key.isAcceptable()) {
                    accept();
                }
            }
        }
    }

    private void accept() throws IOException {
        SocketChannel sc = serverSocketChannel.accept();
        if (sc != null) {
            sc.configureBlocking(false);
            SelectionKey key = sc.register(selector, SelectionKey.OP_READ);
            key.attach(new Session());
        }
    }

    private void readWrite(SelectionKey key) throws IOException {
        Session session = (Session) key.attachment();
        SocketChannel channel = (SocketChannel) key.channel();
        session.processGuessRequest(channel);
    }
}
