package app.insa.clav.Messages;

import java.net.InetAddress;


//Message de type 6
public class MessageChatTxt extends Message{

    public String payload;


    public MessageChatTxt(Message msg, String payload) {
        super(msg);
        this.payload = payload;
    }

    public MessageChatTxt(int typeMessage, InetAddress srcIP, int srcResponsePort, InetAddress destIP, int destPort, String payload) {
        super(typeMessage, srcIP, srcResponsePort, destIP, destPort);
        this.payload = payload;
    }
}
