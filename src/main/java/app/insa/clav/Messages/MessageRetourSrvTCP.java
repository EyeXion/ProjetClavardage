package app.insa.clav.Messages;

public class MessageRetourSrvTCP {

    private MessageChatTxt messageChatTxt;
    private MessageChatFile messageChatFile;
    private Message message;

    public MessageRetourSrvTCP(MessageChatTxt messageChatTxt) {
        this.messageChatTxt = messageChatTxt;
        this.messageChatFile = null;
        this.message = null;
    }

    public MessageRetourSrvTCP(MessageChatFile messageChatFile) {
        this.messageChatFile = messageChatFile;
        this.messageChatTxt = null;
        this.message = null;
    }

    public MessageRetourSrvTCP(Message message) {
        this.messageChatFile = null;
        this.messageChatTxt = null;
        this.message = message;
    }

    public MessageChatTxt getMessageChatTxt() {
        return messageChatTxt;
    }

    public MessageChatFile getMessageChatFile() {
        return messageChatFile;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "MessageRetourSrvTCP{" +
                "messageChatTxt=" + messageChatTxt +
                ", messageChatFile=" + messageChatFile +
                ", message=" + message +
                '}';
    }
}
