package com.varkalys.homework3.client;

import com.varkalys.homework3.server.Controller;
import com.varkalys.homework3.server.Server;
import com.varkalys.homework3.server.model.entity.File;
import com.varkalys.homework3.server.model.entity.User;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.Scanner;

public class ClientImpl extends UnicastRemoteObject implements Client {

    private final static String TCP_HOST = "127.0.0.1";
    private final static int TCP_PORT = 4223;

    private Server server;
    private User user;

    protected ClientImpl() throws RemoteException {
    }

    public void run() throws IOException, NotBoundException, SQLException {
        server = (Server) LocateRegistry.getRegistry(Server.PORT).lookup(Server.NAME);
        printUnauthorizedMenu();
    }

    private void printUnauthorizedMenu() throws IOException, SQLException {
        System.out.println("[1] Login");
        System.out.println("[2] Register");
        Scanner scanner = new Scanner(System.in);
        switch (scanner.nextInt()) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            default:
                System.out.println("Incorrect option. Try again");
                printUnauthorizedMenu();
        }
    }

    private void login() throws IOException, SQLException {
        readUser();
        if (!server.login(user)) {
            System.out.println("Incorrect username and/or password");
            printUnauthorizedMenu();
        } else {
            loggedIn();
        }
    }

    private void readUser() {
        Scanner scanner = new Scanner(System.in);
        user = new User();
        System.out.print("Username: ");
        user.setUsername(scanner.nextLine());
        System.out.print("Password: ");
        user.setPassword(scanner.nextLine());
    }

    private void register() throws IOException, SQLException {
        readUser();
        if (!server.register(user)) {
            System.out.println("User with this username already exists. Try different username");
            register();
        } else {
            loggedIn();
        }
    }

    private void loggedIn() throws IOException, SQLException {
        System.out.println("Successfully logged in");
        server.setFileAccessListener(user, this);
        printLoggedInMenu();
    }

    private void printLoggedInMenu() throws IOException, SQLException {
        System.out.println("[1] Show all files");
        System.out.println("[2] Upload file");
        System.out.println("[3] Delete file");
        System.out.println("[4] Download file");
        Scanner scanner = new Scanner(System.in);
        switch (scanner.nextInt()) {
            case 1:
                printFiles();
                break;
            case 2:
                uploadFile();
                break;
            case 3:
                deleteFile();
                break;
            case 4:
                downloadFile();
                break;
            default:
                System.out.println("Incorrect option. Try again");
        }
        printLoggedInMenu();
    }

    private void printFiles() throws IOException, SQLException {
        File[] files = server.listFiles();
        System.out.println("Files:");
        for (File file : files) {
            System.out.println(file.getName());
        }
    }

    private void uploadFile() throws IOException, SQLException {
        System.out.print("Insert file path: ");
        Scanner scanner = new Scanner(System.in);
        String filePath = scanner.nextLine();
        System.out.print("Is writable? [y/n]: ");
        boolean isWritable = scanner.nextLine().equals("y");
        Path path = Paths.get(filePath);
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        File file = new File();
        file.setName(path.getFileName().toString());
        file.setSize(attr.size());
        file.setOwnerUsername(user.getUsername());
        file.setWritable(isWritable);
        if (server.addFile(file)) {
            uploadFileContents(path);
        }
    }

    private void uploadFileContents(Path path) throws IOException {
        Socket socket = new Socket(TCP_HOST, TCP_PORT);
        Files.copy(path, socket.getOutputStream());
        socket.getOutputStream().flush();
        socket.close();
    }

    @Override
    public void onFileAccessed(File file) throws RemoteException {
        System.out.println("Accessed file " + file.getName());
    }

    private void deleteFile() throws IOException, SQLException {
        printFiles();
        System.out.print("Insert file name to delete: ");
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();
        server.deleteFile(user, fileName);
    }

    private void downloadFile() throws IOException, SQLException {
        printFiles();
        System.out.print("Insert file name to download: ");
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();
        server.downloadFile(user, fileName);
        Socket socket = new Socket(TCP_HOST, TCP_PORT);
        Files.copy(socket.getInputStream(), Paths.get(fileName));
        socket.close();
    }
}
