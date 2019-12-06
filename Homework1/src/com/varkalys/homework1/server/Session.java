package com.varkalys.homework1.server;

import com.varkalys.homework1.protocol.request.GuessRequest;
import com.varkalys.homework1.protocol.request.LoginRequest;
import com.varkalys.homework1.protocol.response.GuessResponse;
import com.varkalys.homework1.protocol.response.LoginResponse;
import com.varkalys.homework1.protocol.util.ProtocolReader;
import com.varkalys.homework1.protocol.util.ProtocolWriter;
import com.varkalys.homework1.server.controller.GuessController;
import com.varkalys.homework1.server.controller.LoginController;

import java.io.IOException;
import java.net.Socket;

public class Session {

    private final Socket socket;
    private final LoginController loginController = new LoginController();
    private final GuessController guessController = new GuessController(loginController);

    public Session(Socket socket) {
        this.socket = socket;
    }

    public void start() {
        new Thread(() -> {
            try {
                processRequests();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void processRequests() throws IOException {
        while (true) {
            if (loginController.isLoggedIn()) {
                processGuessRequest();
            } else {
                processLoginRequest();
            }
        }
    }

    private void processLoginRequest() throws IOException {
        LoginRequest loginRequest = ProtocolReader.readMessage(socket.getInputStream(), new LoginRequest());
        LoginResponse loginResponse = new LoginResponse();
        loginController.login(loginRequest, loginResponse);
        ProtocolWriter.writeMessage(socket.getOutputStream(), loginResponse);
    }

    private void processGuessRequest() throws IOException {
        GuessRequest guessRequest = ProtocolReader.readMessage(socket.getInputStream(), new GuessRequest());
        GuessResponse guessResponse = new GuessResponse();
        guessController.guess(guessRequest, guessResponse);
        ProtocolWriter.writeMessage(socket.getOutputStream(), guessResponse);
    }
}
