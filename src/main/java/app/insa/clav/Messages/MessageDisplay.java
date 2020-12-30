package app.insa.clav.Messages;

import java.io.File;

/**
 * Element of the list that contains the history of messages returned by the DB
 */
public class MessageDisplay {



    private int sourceId;
    private String date;
    private String payload;
    /**
     * Type 1 --> Normal text message
     * Type 2 --> File message not image
     * Type 3 --> Image file (png, jpg, gif, jpeg, svg)
     */
    private int type;

    public MessageDisplay(int sourceId, String date, String payload,int type) {
        this.sourceId = sourceId;
        this.date = date;
        this.payload = payload;
        this.type = type;
    }

    public MessageDisplay(){}

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

