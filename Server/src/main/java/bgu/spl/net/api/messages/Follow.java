package bgu.spl.net.api.messages;

import bgu.spl.net.api.bidi.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Follow extends ClientToServerMessages {

    private int isFollow;
    private int index;
    private int indexForBytes;
    private int numOfSuccessfulActions;
    private byte[] usersNumByte;
    private byte[] bytes;
    private short numOfUsers;
    private ArrayList<String> users;
    private String userName;
    private boolean errorWasSent;


    public Follow() {
        super();
        index = 0;
        bytes = new byte[1<<10];
        usersNumByte = new byte[2];
        numOfUsers=0;
        users = new ArrayList<>();
        indexForBytes = 0;
        userName = "";
        numOfSuccessfulActions = 0;
        errorWasSent = false;
    }

    @Override
    public Object decodeByByte(byte nextByte) {
        if(index == 0) {
            isFollow = nextByte;
            index++;
        }
        else if(index<=2) {
            usersNumByte[index - 1] = nextByte;
            if (index == 2)
                numOfUsers = bytesToShort(usersNumByte);
            index++;
        }
        else {
            if (nextByte != '\0') {
                bytes[indexForBytes] = nextByte;
                indexForBytes++;
            }
            else{
                userName = new String(bytes,0, indexForBytes, StandardCharsets.UTF_8);
                users.add(userName);
                indexForBytes=0;
                if(users.size()==numOfUsers)
                    return this;
            }
        }
        return null;
    }

    private short bytesToShort ( byte[] byteArr)
    {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void proccess(int connectionID, ConnectionsImpl connections, BidiMessagingProtocolImpl protocol) {

        String userName = DataStructure.connectionIDs.get(connectionID);

        // if the user isn't online
        if (userName==null)
            connections.send(connectionID, new Error((short)4));
        else {
            if (!DataStructure.onlineUsers.containsKey(userName)) {
                errorWasSent = true;
                connections.send(connectionID, new Error((short) 4));
            } else {
                User thisUser = DataStructure.onlineUsers.get(userName);
                // if the request is to follow
                if (isFollow == 0) {
                    for (String user : users) {
                        if (DataStructure.registeredUsers.containsKey(user)) {
                            User tmpUser = DataStructure.registeredUsers.get(user);
                            synchronized (thisUser) {
                                if (!thisUser.getFollowing().contains(user)) {
                                    thisUser.getFollowing().add(user);
                                    tmpUser.getFollowers().add(userName);
                                    numOfSuccessfulActions++;
                                }
                            }
                        }
                    }
                }

                // if the request is to unfollow
                else {
                    for (String user : users) {
                        if (DataStructure.registeredUsers.containsKey(user)) {
                            User tmpUser = DataStructure.registeredUsers.get(user);
                            synchronized (thisUser) {
                                if (thisUser.getFollowing().contains(user)) {
                                    thisUser.getFollowing().remove(user);
                                    tmpUser.getFollowers().remove(userName);
                                    numOfSuccessfulActions++;
                                }
                            }
                        }
                    }
                }
            }

            // send an ACK only if the follow unfollow has been done at least once
            if (numOfSuccessfulActions == 0) {
                if (!errorWasSent)
                    connections.send(connectionID, new Error((short) 4));
            } else {
                connections.send(connectionID, new AckFollowOrUserList((short) 4, (short) numOfSuccessfulActions, users));
            }
        }
    }
}
