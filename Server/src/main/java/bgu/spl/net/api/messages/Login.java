package bgu.spl.net.api.messages;

import bgu.spl.net.api.bidi.*;

import java.nio.charset.StandardCharsets;

public class Login extends ClientToServerMessages {

    private String userName;
    private String userPassword;
    private boolean finishUserName;
    private int  counter1;
    private int  counter2;
    private byte[]  bytes;



    public Login() {
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
                userPassword = new String(bytes,counter1, counter2, StandardCharsets.UTF_8);
                return this;
            }
        }
        return  null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void proccess(int connectionId, ConnectionsImpl connections, BidiMessagingProtocolImpl protocol) {

        // if the user never registered to the server
        if (!DataStructure.registeredUsers.containsKey(userName)) {
            connections.send(connectionId, new Error((short) 2));
        }

        // if the user entered a bad password
        else if (!DataStructure.registeredUsers.get(userName).getPassword().equals(userPassword)) {
            connections.send(connectionId, new Error((short) 2));
        }


        // if the user is already online
        else if (DataStructure.onlineUsers.containsKey(userName)) {
            connections.send(connectionId, new Error((short) 2));
        }

        // if the user can login
        else {
            User user = DataStructure.registeredUsers.get(userName);
            synchronized (user) {
                user.setConnectionID(connectionId);
                user.setOnline(true);

                if (DataStructure.occupied.get(connectionId) != null) {
                    if (DataStructure.occupied.get(connectionId))
                        connections.send(connectionId, new Error((short) 2));
                } else {
                    // dealing with all the pending messages
                    if (!user.getPending().isEmpty()) {
                        while (!user.getPending().isEmpty()) {
                            connections.send(connectionId, user.getPending().pop());
                        }
                    }
                    DataStructure.onlineUsers.put(userName, user);
                    DataStructure.connectionIDs.put(connectionId, userName);
                    DataStructure.occupied.put(connectionId, true);
                    connections.send(connectionId, new ACK((short) 2));
                }
            }
        }
    }



}
