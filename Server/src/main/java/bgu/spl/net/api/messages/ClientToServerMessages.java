package bgu.spl.net.api.messages;

import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionsImpl;

public abstract class ClientToServerMessages<T>{


    public ClientToServerMessages() {

    }
    public abstract Object decodeByByte(byte nextByte);

    public abstract void proccess(int connectionID, ConnectionsImpl connections, BidiMessagingProtocolImpl protocol);

}
