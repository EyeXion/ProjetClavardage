package app.insa.clav.Messages;

import java.io.File;

public class MessageDisplayFile extends MessageDisplay{

    private File file;
    private String ext;


    public MessageDisplayFile(int sourceId, String date, String payload, int type, File file, String ext) {
        super(sourceId, date, payload, type);
        this.file = file;
        this.ext = ext;
    }

    public MessageDisplayFile(File file) {
        this.file = file;
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
}
