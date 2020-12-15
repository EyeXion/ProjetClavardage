package app.insa.clav.Messages;

/**
 * Element of the list that contains the history of messages returned by the DB
 */
public class MessageHistoryList {

    private int sourceId;
    private String date;
    private String payload;

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
