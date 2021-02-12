package app.insa.clav.Messages;


public class MessageSrvTCP {
    private int userId;
    private int id;
    private Message message;

    public MessageSrvTCP(int userId, int id, Message message) {
        this.userId = userId;
        this.id = id;
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public Message getMessage() {
        return message;
    }
}
