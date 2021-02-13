package app.insa.clav.Reseau;
import app.insa.clav.Core.Utilisateurs;
import app.insa.clav.Messages.*;
import app.insa.clav.UISubStages.ChatStage;
import javafx.application.Platform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import org.apache.commons.io.FilenameUtils;

/**
 * Manager of the connection for a what room.
 */
public class TCPChatConnection extends Thread{


    /**
     * Buffer dans lequel on met les messages reçus si ils passent le filter
     */
    private final ArrayList<Message> msgReceivedBuffer;


    /**
     * Buffer dans lequel on met les messages reçus pour les fichiers
     */
    private final ArrayList<MessageDisplayFile> msgReceivedBufferFiles;


    private Socket link;

    private ObjectOutputStream objectOutStream;
    private ObjectInputStream objectInStream;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean isOutdoor;
    private ServletConnection serCon;

    PropertyChangeSupport support;

    public int remoteUserId;
    public int localUserId;


    /**
     * Constructeur utilisé quand l'utilisateur distant inititie la connexion
     * @param link
     */
    public TCPChatConnection(Socket link, int remoteUserId, int localUserId,InputStream is, OutputStream os, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream){
        this.link = link;
        this.isOutdoor = false;
        this.objectOutStream = objectOutputStream;
        this.objectInStream = objectInputStream;
        this.dos = new DataOutputStream(os);
        this.dis = new DataInputStream(is);
        this.msgReceivedBuffer = new ArrayList<Message>();
        this.msgReceivedBufferFiles = new ArrayList<MessageDisplayFile>();
        this.remoteUserId = remoteUserId;
        this.localUserId = localUserId;
        this.serCon = ServletConnection.getInstance();
        this.support = new PropertyChangeSupport(this);
        Platform.runLater(() -> new ChatStage(this));
    }

    /**
     * Constructeur utilisé quand c'est l'user local qui initie la connexion
     * @param msgInit
     *                  Message d'authentification que l'on enverra après l'etablissement de connexion
     *                  Message de type 7 classe MessageInit
     */
    public TCPChatConnection(MessageInit msgInit, InetAddress destIP, int portEcouteTCP, int remoteUserId){
        try {
            if (destIP == null) {
                System.out.println("NULLLLLL");
            }
            try {
                this.link = new Socket(destIP, portEcouteTCP);
            }
            catch (IOException e) {
                System.out.println("C'est bien la" + destIP + " " + portEcouteTCP);
            }
            if (this.link == null) {
                System.out.println("LINK NULL");
            }
            OutputStream os = this.link.getOutputStream();
            InputStream is = this.link.getInputStream();
            this.objectOutStream = new ObjectOutputStream(os);
            this.objectInStream = new ObjectInputStream(is);
            this.dos = new DataOutputStream(os);
            this.dis = new DataInputStream(is);
            this.objectOutStream.writeObject(msgInit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.isOutdoor = false;
        this.serCon = ServletConnection.getInstance();
        this.msgReceivedBuffer = new ArrayList<Message>();
        this.msgReceivedBufferFiles = new ArrayList<MessageDisplayFile>();
        this.remoteUserId = remoteUserId;
        this.localUserId = msgInit.id;
        this.support = new PropertyChangeSupport(this);
        Platform.runLater(() -> new ChatStage(this));
    }

    public void startTCPCo() {
        this.start();
    }

    public void setOutdoor() {
        this.isOutdoor = true;
    }

    public boolean isOutdoor() {
        return this.isOutdoor;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl){
        this.support.addPropertyChangeListener("messageTextReceivedTCP",pcl);
        this.support.addPropertyChangeListener("connectionChatClosed",pcl);
        this.support.addPropertyChangeListener("userDisconnected",pcl);
        this.support.addPropertyChangeListener("fileReceived",pcl);
    }



    /**
     * Renvoi le message le plus ancien du buffer et le supprime du buffer
     * @return Le message le plus ancien du buffer
     */
    public Message getMessageReceived(){
        return this.msgReceivedBuffer.remove(0);
    }

    /**
     * Renvoi le message le plus ancien du buffer de files et le supprime du buffer
     * @return Le message le plus ancien du buffer
     */
    public MessageDisplayFile getMessageFileReceived(){
        return this.msgReceivedBufferFiles.remove(0);
    }


    @Override
    public void run() {
        if (!this.isOutdoor) {
            while (true) {
                Message msgReceived = null;
                try {
                    msgReceived = (Message) this.objectInStream.readObject();
                    System.out.println("Message reçu");
                } catch (IOException e) {
                    this.support.firePropertyChange("userDisconnected", true, false);
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (msgReceived.typeMessage == 8) {
                    this.support.firePropertyChange("connectionChatClosed", true, false);
                    break;
                } else if (msgReceived.typeMessage == 9) {
                    int bytes = 0;
                    MessageChatFile msgFile = (MessageChatFile) msgReceived;
                    try {
                        String path;
                        if (localUserId > remoteUserId) {
                            path = "./file_" + remoteUserId + "_" + localUserId + "_" + msgFile.date + "." + msgFile.ext;
                        } else {
                            path = "./file_" + localUserId + "_" + remoteUserId + "_" + msgFile.date + "." + msgFile.ext;
                        }
                        File file = new File(path);
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        long size = msgFile.fileSize;
                        byte[] buffer = new byte[4 * 1024];
                        while (size > 0) {
                            bytes = dis.read(buffer, 0, (int) Math.min(buffer.length, size));
                            fileOutputStream.write(buffer, 0, bytes);
                            size -= bytes;
                        }
                        System.out.println("Reception fichier terminée");
                        int type = 2;
                        switch (msgFile.ext) {
                            case "png":
                            case "gif":
                            case "jpeg":
                            case "svg":
                            case "jpg":
                                type = 3;
                                break;
                        }
                        this.msgReceivedBufferFiles.add(new MessageDisplayFile(this.remoteUserId, msgFile.date, msgFile.payload, type, file, msgFile.ext, -1));
                        this.support.firePropertyChange("fileReceived", true, false);
                        fileOutputStream.close();
                        file.deleteOnExit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    this.msgReceivedBuffer.add(msgReceived);
                    this.support.firePropertyChange("messageTextReceivedTCP", true, false);
                }
            }
        } else {
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MessageRetourSrvTCP msgReceived = this.serCon.GetMessageChat(this.localUserId, this.remoteUserId);
                if (msgReceived != null) {//this.support.firePropertyChange("userDisconnected", true, false);
                    if (msgReceived.getMessage() != null) {
                        this.support.firePropertyChange("connectionChatClosed", true, false);
                        break;
                    } else if (msgReceived.getMessageChatFile() != null) {
                        int bytes = 0;
                        try {
                            String path;
                            if (localUserId > remoteUserId) {
                                path = "./file_" + remoteUserId + "_" + localUserId + "_" + msgReceived.getMessageChatFile().date + "." + msgReceived.getMessageChatFile().ext;
                            } else {
                                path = "./file_" + localUserId + "_" + remoteUserId + "_" + msgReceived.getMessageChatFile().date + "." + msgReceived.getMessageChatFile().ext;
                            }
                            File file = new File(path);
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            long size = msgReceived.getMessageChatFile().fileSize;
                            byte[] buffer = new byte[4 * 1024];
                            while (size > 0) {
                                bytes = dis.read(buffer, 0, (int) Math.min(buffer.length, size));
                                fileOutputStream.write(buffer, 0, bytes);
                                size -= bytes;
                            }
                            System.out.println("Reception fichier terminée");
                            int type = 2;
                            switch (msgReceived.getMessageChatFile().ext) {
                                case "png":
                                case "gif":
                                case "jpeg":
                                case "svg":
                                case "jpg":
                                    type = 3;
                                    break;
                            }
                            this.msgReceivedBufferFiles.add(new MessageDisplayFile(this.remoteUserId, msgReceived.getMessageChatFile().date, msgReceived.getMessageChatFile().payload, type, file, msgReceived.getMessageChatFile().ext, -1));
                            this.support.firePropertyChange("fileReceived", true, false);
                            fileOutputStream.close();
                            file.deleteOnExit();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        this.msgReceivedBuffer.add(msgReceived.getMessageChatTxt());
                        this.support.firePropertyChange("messageTextReceivedTCP", true, false);
                    }
                }
            }
        }
        System.out.println("\n\n\n\n\nFIN DU THREAD\n\n\n\n");
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageTxt(MessageDisplay msgDisp){
        if (!isOutdoor) {
            if (this.link == null) {
                System.out.println("LINK NULL");
            }
            MessageChatTxt msg = new MessageChatTxt(6, this.link.getLocalAddress(), this.link.getInetAddress(), this.link.getPort(), msgDisp.getPayload(), msgDisp.getDate());
            try {
                this.objectOutStream.writeObject(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            MessageChatTxt msg = new MessageChatTxt(6, null, null, 0, msgDisp.getPayload(), msgDisp.getDate());
            this.serCon.SubmitMessageChatTxt(this.remoteUserId, this.localUserId, msg);
        }
    }


    public void sendMessageFile(MessageDisplayFile msgDisp){
        if (!isOutdoor) {
            int bytes = 0;
            MessageChatFile msgStartofFile = new MessageChatFile(9,this.link.getLocalAddress(),this.link.getInetAddress(),this.link.getPort(),msgDisp.getPayload(),msgDisp.getDate(),msgDisp.getFile().length(),msgDisp.getExt());
            try {
                this.objectOutStream.writeObject(msgStartofFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                FileInputStream fis = new FileInputStream(msgDisp.getFile());
                byte[] buffer = new byte[4*1024];
                while ((bytes=fis.read(buffer))!=-1){
                    dos.write(buffer,0,bytes);
                    dos.flush();
                }
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            MessageChatFile msg = new MessageChatFile(9,null, null, 0,msgDisp.getPayload(),msgDisp.getDate(),msgDisp.getFile().length(),msgDisp.getExt());
            this.serCon.SubmitMessageChatFile(this.remoteUserId, this.localUserId, msg);
        }
    }

    /**
     * When receiving a type 8 message, closed the chat connection
     */
    public void sendCloseChat() {
        if (!isOutdoor) {
            Message msg = new Message(8, this.link.getLocalAddress());
            try {
                this.objectOutStream.writeObject(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Message msg = new Message(8, null);
            this.serCon.SubmitMessageChat(this.remoteUserId, this.localUserId, msg);
        }
    }

    public Socket getSocket() {
        return link;
    }
}
