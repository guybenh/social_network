package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;

// as soon as a client gets a socket chanel, it should be added to this data structure
public class ConnectionsImpl<T> implements Connections<T> {

    // a map representing a connection ID and the represented user
    private ConcurrentHashMap<Integer, ConnectionHandler<T>> connectedUsers;
    private Integer connectionID;

    public ConnectionsImpl(){
        connectionID = 0;
        connectedUsers = new ConcurrentHashMap<>();
    }

    @Override
    public boolean send(int connectionId, T msg) {
        synchronized (connectionID) {
            if (!connectedUsers.containsKey(connectionId))
                return false;
            else {
                connectedUsers.get(connectionId).send(msg);
                return true;
            }
        }
    }

    @Override
    public void broadcast(T msg) {
        connectedUsers.forEach((k, v)->v.send(msg));
    }

    @Override
    public void disconnect(int connectionId) {
        synchronized (connectedUsers) {
            connectedUsers.remove(connectionId);
        }
    }


    // adding clients to usersMap
    @SuppressWarnings("unchecked")
    public void addToMap(ConnectionHandler connectionHandler){
        synchronized (connectedUsers) {
            connectedUsers.put(connectionID, connectionHandler);
            connectionID++;
        }
    }


    public Integer getConnectionID() {
        return connectionID;
    }

    public ConcurrentHashMap<Integer, ConnectionHandler<T>> getConnectedUsers() {
        return connectedUsers;
    }
}
