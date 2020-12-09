package app.insa.clav.Reseau;
import app.insa.clav.Core.DataBaseAccess;
import app.insa.clav.Messages.Message;
import app.insa.clav.Messages.MessageChatTxt;
import app.insa.clav.Messages.MessageInit;
import app.insa.clav.UISubStages.ChatStage;
import javafx.application.Platform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class TCPChatConnection extends Thread{


    /**
     * Buffer dans lequel on met les messages reçus si ils passent le filter
     */
    private final ArrayList<Message> msgReceivedBuffer;

    private Socket link;

    private ObjectOutputStream objectOutStream;
    private ObjectInputStream objectInStream;

    PropertyChangeSupport support;

    public int remoteUserId;
    public boolean exit = false;


    /**
     * Constructeur utilisé quand l'utilisateur distant inititie la connexion
     * @param link
     */
    public TCPChatConnection(Socket link, int remoteUserId, ObjectInputStream ois, ObjectOutputStream oos){
        this.link = link;
        this.objectOutStream = oos;
        this.objectInStream = ois;
        this.msgReceivedBuffer = new ArrayList<Message>();
        this.remoteUserId = remoteUserId;
        this.support = new PropertyChangeSupport(this);
        Platform.runLater(() -> new ChatStage(this));
        this.start();
    }

    /**
     * Constructeur utilisé quand c'est l'user local qui initie la connexion
     * @param msgInit
     *                  Message d'authentification que l'on enverra après l'etablissement de connexion
     *                  Message de type 7 classe MessageInit
     */
    public TCPChatConnection(MessageInit msgInit, int remoteUserId){
        try {
            this.link = new Socket(msgInit.destIP,msgInit.destPort);
            this.objectOutStream = new ObjectOutputStream(this.link.getOutputStream());
            this.objectInStream = new ObjectInputStream(this.link.getInputStream());
            this.objectOutStream.writeObject(msgInit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.msgReceivedBuffer = new ArrayList<Message>();
        this.remoteUserId = remoteUserId;
        this.support = new PropertyChangeSupport(this);
        Platform.runLater(() -> new ChatStage(this));
        this.start();
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl){
        this.support.addPropertyChangeListener("messageTextReceivedTCP",pcl);
        this.support.addPropertyChangeListener("connectionChatClosed",pcl);

    }



    /**
     * Renvoi le message le plus ancien du buffer et le supprime du buffer
     * @return Le message le plus ancien du buffer
     */
    public Message getMessageReceived(){
        return this.msgReceivedBuffer.remove(0);
    }

    @Override
    public void run() {
        while (!exit){
            Message msgReceived = null;
            try {
                msgReceived = (Message) this.objectInStream.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (msgReceived.typeMessage == 8){
                this.support.firePropertyChange("connectionChatClosed",true,false);
                try {
                    synchronized (this){
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    exit = true;
                }
            }
            else {
                this.msgReceivedBuffer.add(msgReceived);
                this.support.firePropertyChange("messageTextReceivedTCP", true, false);
            }
        }
    }

    public void sendMessageTxt(String payload){
        MessageChatTxt msg = new MessageChatTxt(6,this.link.getLocalAddress(),this.link.getLocalPort(),this.link.getInetAddress(),this.link.getPort(),payload);
        try {
            this.objectOutStream.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCloseChat() {
        Message msg = new Message(8,this.link.getLocalAddress(),this.link.getLocalPort(),this.link.getInetAddress(),this.link.getPort());
        try {
            this.objectOutStream.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return link;
    }
}
