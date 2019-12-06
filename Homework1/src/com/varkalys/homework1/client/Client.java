package com.varkalys.homework1.client;

import com.varkalys.homework1.protocol.request.GuessRequest;
import com.varkalys.homework1.protocol.request.LoginRequest;
import com.varkalys.homework1.protocol.response.GuessResponse;
import com.varkalys.homework1.protocol.response.LoginResponse;
import com.varkalys.homework1.protocol.util.ProtocolReader;
import com.varkalys.homework1.protocol.util.ProtocolWriter;

import java.io.IOException;
import java.net.Socket;

public class Client {

    private final static String HOST = "127.0.0.1";
    private final static int PORT = 4222;

    private Socket socket;

    public void connect() throws IOException {
        socket = new Socket(HOST, PORT);
    }

    public LoginResponse login(LoginRequest loginRequest) throws IOException {
        ProtocolWriter.writeMessage(socket.getOutputStream(), loginRequest);
        return ProtocolReader.readMessage(socket.getInputStream(), new LoginResponse());
    }

    public GuessResponse guess(GuessRequest guessRequest) throws IOException {
        ProtocolWriter.writeMessage(socket.getOutputStream(), guessRequest);
        return ProtocolReader.readMessage(socket.getInputStream(), new GuessResponse());
    }
}
