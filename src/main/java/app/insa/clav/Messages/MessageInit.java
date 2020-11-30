package app.insa.clav.Messages;

import java.net.InetAddress;

public class MessageInit extends Message{

    int id;


    public MessageInit(int typeMessage, InetAddress srcIP, int srcResponsePort, InetAddress destIP, int destPort, int localId) {
        super(typeMessage, srcIP, srcResponsePort, destIP, destPort);
        this.id = localId;
    }
}
