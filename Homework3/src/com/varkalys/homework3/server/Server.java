package com.varkalys.homework3.server;

import com.varkalys.homework3.client.Client;
import com.varkalys.homework3.server.model.entity.File;
import com.varkalys.homework3.server.model.entity.User;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface Server extends Remote {
    String NAME = "//localhost/MyServer";
    int PORT = 4425;

    boolean register(User user) throws RemoteException, SQLException;
    boolean login(User user) throws RemoteException, SQLException;
    File[] listFiles() throws RemoteException, SQLException;
    boolean addFile(File file) throws RemoteException, SQLException;
    void setFileAccessListener(User user, Client client) throws RemoteException;
    void deleteFile(User user, String fileName) throws IOException, SQLException;
    void downloadFile(User user, String fileName) throws IOException, SQLException;
}
