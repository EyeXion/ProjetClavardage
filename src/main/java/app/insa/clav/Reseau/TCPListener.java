package app.insa.clav.Reseau;

import app.insa.clav.Messages.Message;
import app.insa.clav.Messages.MessageInit;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * TCPListeer that created TCPChatConnection on remote request
 */
public class TCPListener extends Thread{


    private InetAddress localAddress;
    private int port;
    private  ServerSocket servSocket;
    private int localId;

    private PropertyChangeSupport support;

    /**
     * Buffer where the TCPChatConnecition are stored for the model.
     */
    private ArrayList<TCPChatConnection> bufferTCPConnection;

    public TCPListener(InetAddress localAddress,int localId){
        this.localAddress = localAddress;
        try {
            this.servSocket = new ServerSocket(0,1000,localAddress); //0 alloue un port dispo
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.port = servSocket.getLocalPort();
        this.localId = localId;
        this.bufferTCPConnection = new ArrayList<TCPChatConnection>();
        this.support = new PropertyChangeSupport(this);
    }


    /**
     * Ajoute un observateur à qui on va envoyer les notifications de réception de messages
     * @param pcl
     *              Représente un classe qui implémente, PropertyChangeListener, donc un observateur
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl){
        //On va mettre name à "App" pour l'application
        this.support.addPropertyChangeListener("chatCreated",pcl);
    }

    public int getPort() {
        return port;
    }

    public TCPChatConnection getTCPChatConnection(){return this.bufferTCPConnection.remove(0);}

    @Override
    public void run() {
        while (true) {
            try {
                Socket link = servSocket.accept();
                OutputStream os = link.getOutputStream();
                InputStream is = link.getInputStream();
                ObjectOutputStream objectOutStream = new ObjectOutputStream(os);
                ObjectInputStream objectInStream = new ObjectInputStream(is);
                MessageInit msgInit = (MessageInit) objectInStream.readObject();
                int remoteUserId = msgInit.id;
                this.bufferTCPConnection.add(new TCPChatConnection(link,remoteUserId,is,os,objectOutStream,objectInStream));
                this.support.firePropertyChange("chatCreated",true,false);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
