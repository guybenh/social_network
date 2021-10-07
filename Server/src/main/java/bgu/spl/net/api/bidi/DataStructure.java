package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.BlockingConnectionHandler;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DataStructure {

    @SuppressWarnings("unchecked")

    // a map representing registered users
    public volatile static ConcurrentHashMap<String, User> registeredUsers = new ConcurrentHashMap<>();

    // a map representing online users
    public volatile static ConcurrentHashMap<String, User> onlineUsers = new ConcurrentHashMap<>();

    // a map representing users and their connection ID
    public volatile static ConcurrentHashMap<Integer, String> connectionIDs = new ConcurrentHashMap<>();

    // a map representing connection IDs and if the are online
    public volatile static ConcurrentHashMap<Integer, Boolean> occupied = new ConcurrentHashMap<>();


}
