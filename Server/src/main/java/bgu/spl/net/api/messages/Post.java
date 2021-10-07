package bgu.spl.net.api.messages;

import bgu.spl.net.api.bidi.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Post extends ClientToServerMessages {

    private int index;
    private byte[] bytes;
    private String content;
    private ArrayList<String> users;
    private boolean sentAck;

    public Post() {
        super();
        index = 0;
        bytes = new byte[1 << 10];
        content = "";
        users = new ArrayList<>();
        sentAck = false;
    }

    @Override
    public Object decodeByByte(byte nextByte) {
        if (nextByte != '\0') {
            bytes[index] = nextByte;
            index++;
        } else {
            content = new String(bytes, 0, index, StandardCharsets.UTF_8);
            return this;
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void proccess(int connectionID, ConnectionsImpl connections, BidiMessagingProtocolImpl protocol) {
        String userName = DataStructure.connectionIDs.get(connectionID);
        if (userName == null)
            connections.send(connectionID, new Error((short) 5));
        else {
            // if the user isn't online
            if (!DataStructure.onlineUsers.containsKey(userName)) {
                connections.send(connectionID, new Error((short) 5));
            } else {
                User thisUser = DataStructure.onlineUsers.get(userName);
                // adding the users names that have "@"
                String[] parsed = content.split(" ");
                for (String s : parsed) {
                    if (s.contains("@")) {
                        if (!users.contains(s.substring(1)))
                            users.add(s.substring(1));
                    }
                }
                synchronized (thisUser) {
                    if (thisUser.getFollowers().isEmpty()) {
                        sentAck = true;
                        connections.send(connectionID, new ACK((short) 5));
                    } else {
                        // sending a message to the followers
                        for (String follower : thisUser.getFollowers()) {
                            User tmpUser = DataStructure.registeredUsers.get(follower);
                            send(thisUser, tmpUser, connectionID, connections);
                        }
                    }
                    // sending a message to the "@"
                    for (String tagged : users) {
                        User tmpUser = DataStructure.registeredUsers.get(tagged);
                        // checking if the "@" user is registered
                        if (tmpUser != null) {
                            if (DataStructure.registeredUsers.containsKey(tmpUser.getName())) {
                                // checking if the "@" user isn't already in the followers list
                                // if he isn't - send him the message (we don't want to send a message to a users who is on the following list twice)
                                if (!thisUser.getFollowers().contains(tmpUser.getName())) {
                                    send(thisUser, tmpUser, connectionID, connections);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void send(User thisUser, User tmpUser, int connectionID, Connections connections) {
        // the follower is online
        synchronized (tmpUser) {
            if (tmpUser.isOnline()) {
                thisUser.getMessages().getPublicPosts().add(content);
                if (!sentAck) {
                    connections.send(connectionID, new ACK((short) 5));
                }
                connections.send(tmpUser.getConnectionID(), new Notifications('1', thisUser.getName(), content));
            }
            // the follower is not online
            else {
                thisUser.getMessages().getPublicPosts().add(content);
                tmpUser.getPending().add(new Notifications('1', thisUser.getName(), content));
                if (!sentAck)
                    connections.send(connectionID, new ACK((short) 5));
            }
        }
    }
}

