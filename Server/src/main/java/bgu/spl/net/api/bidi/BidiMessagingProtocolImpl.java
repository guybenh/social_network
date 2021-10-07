package bgu.spl.net.api.bidi;

import bgu.spl.net.api.messages.ClientToServerMessages;
import bgu.spl.net.api.messages.Logout;

public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocolA<T> {

    private ConnectionsImpl<T> connections;
    private int connectionId;
    private boolean shouldTerminate;



    @Override
    public void start(int connectionId, ConnectionsImpl<T> connections) {
        this.connections = connections;
        this.connectionId = connectionId;
        this.shouldTerminate = false;
    }

    @Override
    public void process(T msg) {
        ((ClientToServerMessages)msg).proccess(this.connectionId, this.connections, this);
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }


    public void setShouldTerminate(boolean shouldTerminate) {
        this.shouldTerminate = shouldTerminate;
    }


}