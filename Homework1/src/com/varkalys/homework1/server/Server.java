package com.varkalys.homework1.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final static int PORT = 4222;
    
    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            Session session = new Session(clientSocket);
            session.start();
        }
    }
}
