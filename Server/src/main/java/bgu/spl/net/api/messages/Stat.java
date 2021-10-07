package bgu.spl.net.api.messages;

import bgu.spl.net.api.bidi.*;

import java.nio.charset.StandardCharsets;

public class Stat extends ClientToServerMessages {

    private int index;
    private byte[] bytes;
    private String userName;

    public Stat() {
        super();
        index = 0;
        bytes = new byte[1 << 10];
        userName = "";
    }

    @Override
    public Object decodeByByte(byte nextByte) {
        if (nextByte != '\0') {
            bytes[index] = nextByte;
            index++;
        } else {
            userName = new String(bytes, 0, index, StandardCharsets.UTF_8);
            return this;
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void proccess(int connectionID, ConnectionsImpl connections, BidiMessagingProtocolImpl protocol) {

        // if the user isn't online
        String ThisUserName = DataStructure.connectionIDs.get(connectionID);
        if (ThisUserName == null)
            connections.send(connectionID, new Error((short) 8));
        else {
            if (!DataStructure.onlineUsers.containsKey(ThisUserName)) {
                connections.send(connectionID, new Error((short) 8));
            }
            //if userName !registered, error
            if (!DataStructure.registeredUsers.containsKey(userName))
                connections.send(connectionID, new Error((short) 8));
            else {
                //return ACK
                User otherUser = DataStructure.registeredUsers.get(userName);
                connections.send(connectionID, new AckStat((short) 8, otherUser.getMessages().getPublicPosts().size()
                        , otherUser.getFollowers().size(), otherUser.getFollowing().size()));
            }
        }
    }
}
