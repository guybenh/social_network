package bgu.spl.net.api.bidi;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.messages.*;
import bgu.spl.net.api.messages.Error;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int index = 0;
    private ClientToServerMessages messageAbs;


    @Override
    public Object decodeNextByte(byte nextByte) {
        bytes[index] = nextByte;
        index++;
        if (index == 2) {
            messageAbs = typeOfMessage();
            if (messageAbs instanceof UserList | messageAbs instanceof Logout) {
                Object nextMessage = getObject(nextByte);
                if (nextMessage != null) return nextMessage;
            } else
                return null;
        }
        if (index > 2) {
            Object nextMessage = getObject(nextByte);
            if (nextMessage != null) return nextMessage;
        }
        return null;
    }

    // auxiliary
    private Object getObject(byte nextByte) {
        Object nextMessage = messageCreator(nextByte, messageAbs);
        if (nextMessage != null) {
            bytes = new byte[1 << 10];
            index = 0;
            return nextMessage;
        }
        return null;
    }


    @Override
    public byte[] encode(Object message) {
        if(message instanceof AckFollowOrUserList)
            return ((AckFollowOrUserList)message).encode();
        if (message instanceof AckStat)
            return ((AckStat)message).encode();
        if(message instanceof ACK)
            return ((ACK)message).encode();
        if(message instanceof Notifications)
            return ((Notifications)message).encode();
        if(message instanceof Error)
            return ((bgu.spl.net.api.messages.Error)message).encode();
        return null;
    }


    private ClientToServerMessages typeOfMessage() {

        switch (bytesToShort(bytes)) {
            case 1:
                return new Register();
            case 2:
                return new Login();
            case 3:
                return new Logout();
            case 4:
                return new Follow();
            case 5:
                return new Post();
            case 6:
                return new PM();
            case 7:
                return new UserList();
            case 8:
                return new Stat();
            default:
                return null;

        }
    }
    private Object messageCreator(byte nextByte, ClientToServerMessages messageAbs){
        return messageAbs.decodeByByte(nextByte);
    }

    private short bytesToShort ( byte[] byteArr)
    {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }


}

