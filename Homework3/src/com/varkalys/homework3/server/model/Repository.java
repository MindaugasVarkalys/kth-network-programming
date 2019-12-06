package com.varkalys.homework3.server.model;

import com.varkalys.homework3.server.model.entity.File;
import com.varkalys.homework3.server.model.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Repository {

    private Connection con;

    public Repository() throws SQLException {
        String connectionUrl = "jdbc:sqlite:C:/Users/minda/Programs/KTH/Network Programming/Homework3/hw3.db";
        con = DriverManager.getConnection(connectionUrl);
    }

    public boolean isUsernameExists(String username) throws SQLException {
        PreparedStatement statement = con.prepareStatement("SELECT * FROM Users WHERE Username = ?");
        statement.setString(1, username);
        return statement.executeQuery().next();
    }

    public void insertUser(User user) throws SQLException {
        PreparedStatement statement = con.prepareStatement("INSERT INTO Users (Username, Password) VALUES (?,?)");
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        statement.execute();
    }

    public boolean isPasswordValid(User user) throws SQLException {
        PreparedStatement statement = con.prepareStatement("SELECT * FROM Users WHERE Username = ? AND Password = ?");
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        return statement.executeQuery().next();
    }

    public File[] selectAllFiles() throws SQLException {
        Statement statement = con.createStatement();
        ResultSet results = statement.executeQuery("SELECT * FROM Files");
        ArrayList<File> files = new ArrayList<>();
        while (results.next()) {
            File file = new File();
            file.setName(results.getString(2));
            file.setSize(results.getInt(3));
            file.setOwnerUsername(results.getString(4));
            file.setWritable(results.getBoolean(5));
            files.add(file);
        }
        return files.toArray(new File[0]);
    }

    public File selectFileByName(String name) throws SQLException {
        PreparedStatement statement = con.prepareStatement("SELECT * FROM Files WHERE Name = ?");
        statement.setString(1, name);
        ResultSet results = statement.executeQuery();
        if (results.next()) {
            File file = new File();
            file.setName(results.getString(2));
            file.setSize(results.getInt(3));
            file.setOwnerUsername(results.getString(4));
            file.setWritable(results.getBoolean(5));
            return file;
        }
        return null;
    }

    public void deleteFileByName(String name) throws SQLException {
        PreparedStatement statement = con.prepareStatement("DELETE FROM Files WHERE Name = ?");
        statement.setString(1, name);
        statement.execute();
    }

    public void insertFile(File file) throws SQLException {
        PreparedStatement statement = con.prepareStatement("INSERT INTO Files (Name, Size, OwnerUsername, Permissions) VALUES (?,?,?,?)");
        statement.setString(1, file.getName());
        statement.setLong(2, file.getSize());
        statement.setString(3, file.getOwnerUsername());
        statement.setBoolean(4, file.isWritable());
        statement.execute();
    }
}
