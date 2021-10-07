package bgu.spl.net.api.messages;

import bgu.spl.net.api.bidi.*;

import java.nio.charset.StandardCharsets;

public class PM extends ClientToServerMessages {

    private String userName;
    private String content;
    private boolean finishUserName;
    private int  counter1;
    private int  counter2;
    private byte[]  bytes;


    public PM() {
        super();
        this.finishUserName = false;
        counter1=0;
        counter2=0;
        bytes = new byte[1<<10];
    }

    public Object decodeByByte (byte nextByte){
        if(!finishUserName) {
            if (nextByte != '\0') {
                bytes[counter1] = nextByte;
                counter1++;
            } else{
                finishUserName = true;
                counter2 = counter1;
            }
        }
        else{
            if (nextByte != '\0') {
                bytes[counter2] = nextByte;
                counter2++;
            } else{
                userName = new String(bytes, 0, counter1, StandardCharsets.UTF_8);
                content = new String(bytes,counter1, counter2, StandardCharsets.UTF_8);
                return this;
            }
        }
        return  null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void proccess(int connectionID, ConnectionsImpl connections, BidiMessagingProtocolImpl protocol) {
        String thisUserName = DataStructure.connectionIDs.get(connectionID);
        if (thisUserName == null)
            connections.send(connectionID, new Error((short) 6));
        else {
            // if the user isn't online
            if (!DataStructure.onlineUsers.containsKey(thisUserName)) {
                connections.send(connectionID, new Error((short) 6));

            } else {
                User thisUser = DataStructure.onlineUsers.get(thisUserName);

                synchronized (thisUser) {
                    // check if the other user is registered
                    if (!DataStructure.registeredUsers.containsKey(userName)) {
                        connections.send(connectionID, new Error((short) 6));
                    }
                    // the other user is registered
                    else {
                        User tmpUser = DataStructure.registeredUsers.get(userName);
                        // the other user is online
                        if (DataStructure.onlineUsers.containsKey(userName)) {
                            thisUser.getMessages().getPrivatePosts().add(content);
                            connections.send(connectionID, new ACK((short) 6));
                            connections.send(tmpUser.getConnectionID(), new Notifications('0', thisUser.getName(), content));
                        }
                        // the user other isn't online
                        else {
                            thisUser.getMessages().getPrivatePosts().add(content);
                            tmpUser.getPending().add(new Notifications('0', thisUser.getName(), content));
                            connections.send(connectionID, new ACK((short) 6));
                        }
                    }
                }
            }
        }
    }
}
