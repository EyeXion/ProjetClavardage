package app.insa.clav.Messages;

import java.net.InetAddress;


/**
 * Messages texte dans un chat
 */
//Message de type 6
public class MessageChatTxt extends Message{

    public String payload;
    public String date;


    public MessageChatTxt(Message msg, String payload, String date) {
        super(msg);
        this.payload = payload;
        this.date = date;
    }

    public MessageChatTxt(int typeMessage, InetAddress srcIP, InetAddress destIP, int destPort, String payload, String date) {
        super(typeMessage, srcIP);
        this.payload = payload;
        this.date = date;
    }
}
