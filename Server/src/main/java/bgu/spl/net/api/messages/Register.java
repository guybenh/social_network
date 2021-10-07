package bgu.spl.net.api.messages;

import bgu.spl.net.api.bidi.*;

import java.nio.charset.StandardCharsets;

public class Register extends ClientToServerMessages {

    private String userName;
    private String userPassword;
    private boolean finishUserName;
    private int counter1;
    private int counter2;
    private byte[] bytes;
    private Object key;


    public Register() {
        super();
        this.finishUserName = false;
        counter1 = 0;
        counter2 = 0;
        bytes = new byte[1 << 10];
        key = new Object();
    }

    public Object decodeByByte(byte nextByte) {
        if (!finishUserName) {
            if (nextByte != '\0') {
                bytes[counter1] = nextByte;
                counter1++;
            } else {
                finishUserName = true;
                counter2 = counter1;
            }
        } else {
            if (nextByte != '\0') {
                bytes[counter2] = nextByte;
                counter2++;
            } else {
                userName = new String(bytes, 0, counter1, StandardCharsets.UTF_8);
                userPassword = new String(bytes, counter1, counter2, StandardCharsets.UTF_8);
                return this;
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void proccess(int connectionID, ConnectionsImpl connections, BidiMessagingProtocolImpl protocol) {

        // if a user isn't registered to the server
        if (!DataStructure.registeredUsers.containsKey(userName)) {
            User user = new User();
            user.setPassword(userPassword);
            user.setName(userName);
            if (DataStructure.registeredUsers.putIfAbsent(userName, user) == null) {
                connections.send(connectionID, new ACK((short) 1));
            }
            // if he is already registered to the server
            else
                connections.send(connectionID, new Error((short) 1));
        }

        // if he is already registered to the server
        else
            connections.send(connectionID, new Error((short) 1));
    }
}
