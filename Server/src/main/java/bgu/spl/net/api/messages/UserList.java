package bgu.spl.net.api.messages;

import bgu.spl.net.api.bidi.*;

import java.util.ArrayList;

public class UserList extends ClientToServerMessages {

    private short numOfUsers;
    private ArrayList<String> users;

    public UserList() {
        super();
        users = new ArrayList<>();
        numOfUsers = (short) DataStructure.registeredUsers.size();
        for (User user : DataStructure.registeredUsers.values()) {
            users.add(user.getName());
        }
    }

    @Override
    public Object decodeByByte(byte nextByte) {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void proccess(int connectionID, ConnectionsImpl connections, BidiMessagingProtocolImpl protocol) {
        String userName = DataStructure.connectionIDs.get(connectionID);

        // if the user isn't online
        if (userName == null)
            connections.send(connectionID, new Error((short) 7));
        else if (!DataStructure.onlineUsers.containsKey(userName)) {
            connections.send(connectionID, new Error((short) 7));
        }
        // if the user is online
        else
            connections.send(connectionID, new AckFollowOrUserList((short) 7, numOfUsers, users));
    }
}
