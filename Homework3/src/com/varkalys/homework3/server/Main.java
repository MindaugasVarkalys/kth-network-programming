package com.varkalys.homework3.server;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws MalformedURLException, RemoteException, SQLException {
	    Controller.start();
    }
}
