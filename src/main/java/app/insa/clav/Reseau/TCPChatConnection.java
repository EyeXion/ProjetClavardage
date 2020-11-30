package app.insa.clav.Reseau;
import app.insa.clav.Messages.Message;
import app.insa.clav.Messages.MessageInit;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class TCPChatConnection extends Thread{


    /**
     * Buffer dans lequel on met les messages reçus si ils passent le filter
     */
    private ArrayList<Message> msgReceivedBuffer;

    private Socket link;

    private ObjectOutputStream objectOutStream;
    private ObjectInputStream objectInStream;

    PropertyChangeSupport support;

    public int remoteUserId;


    /**
     * Constructeur utilisé quand l'utilisateur distant inititie la connexion
     * @param link
     */
    public TCPChatConnection(Socket link){
        this.link = link;
        try {
            this.objectOutStream = new ObjectOutputStream(this.link.getOutputStream());
            this.objectInStream = new ObjectInputStream(this.link.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.msgReceivedBuffer = new ArrayList<Message>();
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
        this.remoteUserId = remoteUserId;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl){
        this.support.addPropertyChangeListener("TCPReceived",pcl);
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
        while (true){
            Message msgReceived = null;
            try {
                msgReceived = (Message) this.objectInStream.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            this.msgReceivedBuffer.add(msgReceived);
            this.support.firePropertyChange("TCPReceived",true,false);
        }
    }

    public void sendMessage(Message msg){
        try {
            this.objectOutStream.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
