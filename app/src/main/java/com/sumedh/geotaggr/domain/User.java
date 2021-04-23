package com.sumedh.geotaggr.domain;

public class User {
    private String facebookId;
    private String name;

    public User(String facebookId, String name) {
        this.facebookId = facebookId;
        this.name = name;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "<User: " + facebookId + " | " + name + ">";
    }
}
