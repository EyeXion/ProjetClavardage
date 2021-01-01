package app.insa.clav.Messages;

import java.io.File;

public class MessageDisplayFile extends MessageDisplay{

    private File file;
    private String ext;
    private int DBId;



    public MessageDisplayFile(int sourceId, String date, String payload, int type, File file, String ext, int DBid) {
        super(sourceId, date, payload, type);
        this.file = file;
        this.ext = ext;
        this.DBId = DBid;
    }

    public MessageDisplayFile() {
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public int getDBId() {
        return DBId;
    }

    public void setDBId(int DBId) {
        this.DBId = DBId;
    }


}
