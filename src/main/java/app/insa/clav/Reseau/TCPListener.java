package app.insa.clav.Reseau;

import app.insa.clav.Messages.Message;
import app.insa.clav.Messages.MessageInit;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TCPListener extends Thread{


    private InetAddress inetAddress;
    private int port;
    private  ServerSocket servSocket;

    private PropertyChangeSupport support;

    private ArrayList<TCPChatConnection> bufferTCPConnection;

    public TCPListener(InetAddress inetAddress, int tcpListenerPort){
        this.inetAddress = inetAddress;
        try {
            this.servSocket = new ServerSocket(tcpListenerPort,1000,inetAddress); //0 alloue un port dispo
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.port = servSocket.getLocalPort();
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


    public TCPChatConnection getTCPChatConnection(){return this.bufferTCPConnection.remove(0);}

    @Override
    public void run() {
        while (true) {
            try {
                Socket link = servSocket.accept();
                ObjectOutputStream objectOutStream = new ObjectOutputStream(link.getOutputStream());
                ObjectInputStream objectInStream = new ObjectInputStream(link.getInputStream());
                MessageInit msgInit = (MessageInit) objectInStream.readObject();
                int remoteUserId = msgInit.id;
                this.bufferTCPConnection.add(new TCPChatConnection(link,remoteUserId,objectInStream,objectOutStream));
                this.support.firePropertyChange("chatCreated",true,false);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
