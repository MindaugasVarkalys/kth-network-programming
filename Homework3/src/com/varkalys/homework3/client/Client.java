package com.varkalys.homework3.client;

import com.varkalys.homework3.server.model.entity.File;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
    void onFileAccessed(File file) throws RemoteException;
}
