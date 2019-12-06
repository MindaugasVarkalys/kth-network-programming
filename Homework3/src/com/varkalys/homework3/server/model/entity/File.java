package com.varkalys.homework3.server.model.entity;

import java.io.Serializable;

public class File implements Serializable {

    private String name;
    private long size;
    private String ownerUsername;
    private boolean writable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public boolean canWrite(String username) {
        return username.equals(ownerUsername) || writable;
    }

    public boolean isWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }
}
