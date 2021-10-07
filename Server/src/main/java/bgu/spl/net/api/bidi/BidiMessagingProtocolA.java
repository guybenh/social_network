package bgu.spl.net.api.bidi;

import bgu.spl.net.api.MessageEncoderDecoder;

import java.io.BufferedOutputStream;

public interface BidiMessagingProtocolA<T>  {
	/**
	 * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
	**/
    void start(int connectionId, ConnectionsImpl<T> connections);
    
    void process(T msg);
	
	/**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();
}
