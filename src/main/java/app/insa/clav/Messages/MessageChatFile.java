package app.insa.clav.Messages;

import java.net.InetAddress;

public class MessageChatFile extends MessageChatTxt{

    public long fileSize;
    public String ext;

    public MessageChatFile(Message msg, String payload, String date, long fileSize, String ext) {
        super(msg, payload, date);
        this.fileSize = fileSize;
        this.ext = ext;
    }

    public MessageChatFile(int typeMessage, InetAddress srcIP, int srcResponsePort, InetAddress destIP, int destPort, String payload, String date, long fileSize, String ext) {
        super(typeMessage, srcIP, srcResponsePort, destIP, destPort, payload, date);
        this.fileSize = fileSize;
        this.ext = ext;
    }
}
