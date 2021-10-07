package bgu.spl.net.api.bidi;

import bgu.spl.net.api.messages.Notifications;
import bgu.spl.net.api.messages.ServerToClientMessages;

import java.util.ArrayList;
import java.util.LinkedList;

// an object representing a user
public class User {

// ----------- Fields ----------- \\
    private String name;
    private String password;
    private ArrayList<String> following;
    private ArrayList<String> followers;
    private LinkedList<Notifications> pending;
    private MessagePair messages;
    private Integer connectionID;
    private boolean isOnline;

// ----------- Constructor ----------- \\
    public User() {
        this.name = name;
        this.password = "";
        this.following = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.pending = new LinkedList<>();
        this.messages = new MessagePair();
        this.isOnline = false;
    }

// ----------- Constructor ----------- \\


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public MessagePair getMessages() {
        return messages;
    }

    public void setMessages(MessagePair messages) {
        this.messages = messages;
    }

    public Integer getConnectionID() {
        return connectionID;
    }

    public void setConnectionID(Integer connectionID) {
        this.connectionID = connectionID;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public LinkedList<Notifications> getPending() {
        return pending;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
