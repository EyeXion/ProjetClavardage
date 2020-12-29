package app.insa.clav.Messages;

import java.io.File;

public class MessageDisplayFile extends MessageDisplay{

    private File file;

    public MessageDisplayFile(int sourceId, String date, String payload, int type, File file) {
        super(sourceId, date, payload, type);
        this.file = file;
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
}
