package com.varkalys.homework3.client;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, NotBoundException, SQLException {
        ClientImpl client = new ClientImpl();
        client.run();
    }
}
