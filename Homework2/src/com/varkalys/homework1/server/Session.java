package com.varkalys.homework1.server;

import com.varkalys.homework1.protocol.request.GuessRequest;
import com.varkalys.homework1.protocol.response.GuessResponse;
import com.varkalys.homework1.protocol.util.ProtocolReader;
import com.varkalys.homework1.protocol.util.ProtocolWriter;
import com.varkalys.homework1.server.controller.GuessController;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class Session {

    private final GuessController guessController = new GuessController();

    public void processGuessRequest(SocketChannel socketChannel) throws IOException {
        GuessRequest guessRequest = ProtocolReader.readMessage(socketChannel, new GuessRequest());
        GuessResponse guessResponse = new GuessResponse();
        guessController.guess(guessRequest, guessResponse);
        ProtocolWriter.writeMessage(socketChannel, guessResponse);
    }
}
