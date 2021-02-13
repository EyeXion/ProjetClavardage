package app.insa.clav.Messages;


public class MessageSrvTCP {
    private int userId;
    private int id;
    private MessageRetourSrvTCP messageRetourSrvTCP;
    private MessageInit messageInit;

    public MessageSrvTCP(int userId, int id, MessageRetourSrvTCP messageRetourSrvTCP, MessageInit messageInit) {
        this.userId = userId;
        this.id = id;
        this.messageRetourSrvTCP = messageRetourSrvTCP;
        this.messageInit = messageInit;
    }

    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public MessageRetourSrvTCP getMessageRetourSrvTCP() {
        return messageRetourSrvTCP;
    }

    public MessageInit getMessageInit() {
        return messageInit;
    }

    @Override
    public String toString() {
        return "MessageSrvTCP{" +
                "userId=" + userId +
                ", id=" + id +
                ", messageRetourSrvTCP=" + messageRetourSrvTCP +
                ", messageInit=" + messageInit +
                '}';
    }
}
