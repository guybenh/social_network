package bgu.spl.net.api.messages;

import bgu.spl.net.api.bidi.*;

public class Logout extends ClientToServerMessages {

    public Logout() {
        super();
    }

    public Object decodeByByte (byte nextByte){
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void proccess(int connectionID, ConnectionsImpl connections, BidiMessagingProtocolImpl protocol) {
        if (DataStructure.onlineUsers.isEmpty()) {
            connections.send(connectionID, new Error((short)3));
        }
        else {
            // find the user with the current connection ID to remove from the online users map
            String userName = DataStructure.connectionIDs.get(connectionID);
            User user = DataStructure.registeredUsers.get(userName);
            synchronized (user) {
                connections.send(connectionID, new ACK((short) 3));
                //removing user & connectionID
                DataStructure.onlineUsers.remove(userName);
                DataStructure.connectionIDs.remove(connectionID);
                DataStructure.occupied.replace(connectionID, false);
                connections.getConnectedUsers().remove(connectionID);
                user.setOnline(false);
                //shuting down the socket
                protocol.setShouldTerminate(true);
            }
        }
    }
}
