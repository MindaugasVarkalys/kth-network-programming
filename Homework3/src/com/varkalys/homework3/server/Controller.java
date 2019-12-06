package com.varkalys.homework3.server;

import com.varkalys.homework3.client.Client;
import com.varkalys.homework3.server.model.Repository;
import com.varkalys.homework3.server.model.entity.File;
import com.varkalys.homework3.server.model.entity.User;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.HashMap;

public class Controller extends UnicastRemoteObject implements Server {

    private final static int TCP_PORT = 4223;
    private final static String FILES_DIR = "C:\\Users\\minda\\Programs\\KTH\\Network Programming\\Homework3\\files\\";

    private HashMap<String, Client> clients = new HashMap<>();
    private Repository repository = new Repository();

    public static void start() throws RemoteException, MalformedURLException, SQLException {
        Registry registry = LocateRegistry.createRegistry(PORT);
        registry.rebind(NAME, new Controller());
    }

    private Controller() throws RemoteException, SQLException {
        super();
    }

    @Override
    public boolean register(User user) throws SQLException {
        if (!repository.isUsernameExists(user.getUsername())) {
            repository.insertUser(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean login(User user) throws SQLException {
        return repository.isPasswordValid(user);
    }

    @Override
    public File[] listFiles() throws RemoteException, SQLException {
        return repository.selectAllFiles();
    }

    @Override
    public boolean addFile(File file) throws SQLException, RemoteException {
        File existingFile = repository.selectFileByName(file.getName());
        if (existingFile != null && existingFile.canWrite(file.getOwnerUsername())) {
            return false;
        }

        if (existingFile != null) {
            repository.deleteFileByName(file.getName());
            Client client = clients.get(existingFile.getOwnerUsername());
            if (client != null && !existingFile.getOwnerUsername().equals(file.getOwnerUsername())) {
                client.onFileAccessed(file);
            }
            file.setOwnerUsername(existingFile.getOwnerUsername());
        }
        repository.insertFile(file);
        acceptFileUpload(file);
        return true;
    }

    private void acceptFileUpload(File file) {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(TCP_PORT);
                Socket clientSocket = serverSocket.accept();
                Files.copy(clientSocket.getInputStream(), Paths.get(FILES_DIR + file.getName()));
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    @Override
    public void setFileAccessListener(User user, Client client) throws RemoteException {
        clients.put(user.getUsername(), client);
    }

    @Override
    public void deleteFile(User user, String fileName) throws IOException, SQLException {
        File file = repository.selectFileByName(fileName);
        if (file.canWrite(user.getUsername())) {
            repository.deleteFileByName(fileName);
            Files.delete(Paths.get(FILES_DIR + fileName));
        }
    }

    @Override
    public void downloadFile(User user, String fileName) throws IOException, SQLException {
        acceptFileDownload(fileName);
    }

    private void acceptFileDownload(String fileName) {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(TCP_PORT);
                Socket clientSocket = serverSocket.accept();
                Files.copy(Paths.get(FILES_DIR + fileName), clientSocket.getOutputStream());
                clientSocket.getOutputStream().flush();
                clientSocket.getOutputStream().close();
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
