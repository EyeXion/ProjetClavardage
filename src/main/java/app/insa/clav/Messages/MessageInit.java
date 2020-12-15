package app.insa.clav.Messages;

import java.net.InetAddress;

/**
 * Messaged sent when a user creates a chat room to identifies itself
 */
public class MessageInit extends Message{

    public int id;


    public MessageInit(int typeMessage, InetAddress srcIP, int srcResponsePort, InetAddress destIP, int destPort, int localId) {
        super(typeMessage, srcIP, srcResponsePort, destIP, destPort);
        this.id = localId;
    }
}
