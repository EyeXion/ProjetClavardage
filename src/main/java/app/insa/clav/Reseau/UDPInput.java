package app.insa.clav.Reseau;

import app.insa.clav.Messages.Message;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;


/**
 * Classe qui permet d'écouter des messages en UDP. Fonctionne dans un thread à part
 */
public class UDPInput extends Thread{

    /**
     * Socket par lequel on va recevoir les paquets
     */
    private DatagramSocket socket;
    /**
     * Buffer dans lequel on met les messages reçus si ils passent le filter
     */
    private ArrayList<Message> msgReceivedBuffer;
    /**
     * Permet d'implémenter le DP Observateur. Cette classe est l'obeservable.
     * Cet attribut permet de notifier les observateurs (ici le modèle, soit l'Application)
     */
    private PropertyChangeSupport support;
    /**
     * Filtre qui permet de filtrer les messages entrants. L'index est en correspondance avec le type de message.
     * i.e. filter[1] pour message de type 1. Si filter à vrai, message va dans le buffer et on notifie l'application
     */
    private boolean[] filter;

    //Créer un attribut filtre que l'application pourra modifier. Selon le filtre, des notifications seront envoyées à l'application ou non


    /**
     * Constructeur
     * @param listeningPort
     *                  Numéro de port de la machine locale sur lequel on écoute (le même sur toutes les machines en théorie)
     */
    public UDPInput(int listeningPort){
        try {
            this.socket = new DatagramSocket(listeningPort);
            //System.out.println("\n\nADRESSE DU SOCKET : " + localAddress.toString());
            this.filter = new boolean[8];
            this.filter[1] = true;
            this.filter[2] = false;
            this.filter[3] = false;
            this.filter[4] = true;
            this.filter[7] = true;
            this.socket.setBroadcast(true);
            this.msgReceivedBuffer = new ArrayList<Message>();
            this.support = new PropertyChangeSupport(this);
        }
        catch (SocketException e){
            System.out.println("Exception creation Socket UDP input");
        }
    }

    /**
     * Ajoute un observateur à qui on va envoyer les notifications de réception de messages
     * @param pcl
     *              Représente un classe qui implémente, PropertyChangeListener, donc un observateur
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl){
        //On va mettre name à "App" pour l'application
        this.support.addPropertyChangeListener("UDPInput",pcl);
    }

    /**
     * Renvoi le message le plus ancien du buffer et le supprime du buffer
     * @return Le message le plus ancien du buffer
     */
    public Message getMessageReceived(){
        return this.msgReceivedBuffer.remove(0);
    }


    /**
     * Change les valeurs du filtre
     * @param index
     *              Numéro du type de message à filtrer
     * @param value
     *              Vrai ou faux selon si on veut laisser passer le message ou non (vrai -> message passe)
     */
    public void setFilterValue(int index, boolean value){
        this.filter[index] = value;
    }

    public void printFilter() {
        int i;
        for (i=0;i<8;i++) {
            System.out.println("Filtre de type " + i + " : " + this.filter[i]);
        }
    }

    /**
     * Ecoute en permanance le socket UDP et si un message reçu passe le filtre, on notifie les observateurs
     */
    @Override
    public void run(){
        byte[] buffer = new byte[9000];
        while(true){
            DatagramPacket inputPacket = new DatagramPacket(buffer,buffer.length);
            try
            {
                //System.out.println("En attente de reception d'un message");
                this.socket.receive(inputPacket);
                //System.out.println("Message reçu");
            }
            catch (IOException e){
                System.out.println("IOException reception paquet UDP");
                //e.printStackTrace();
            }
            ByteArrayInputStream byteInStream = new ByteArrayInputStream(inputPacket.getData());
            try {
                ObjectInputStream objectInStream = new ObjectInputStream(byteInStream);
                Message msg = (Message) objectInStream.readObject();
                if (this.filter[msg.typeMessage]){
                    this.msgReceivedBuffer.add(msg);
                    this.support.firePropertyChange("UDPInput",this.msgReceivedBuffer.size() -1, this.msgReceivedBuffer.size());
                } else {
                    System.out.println("Message filtré : " + msg.toString());
                }
            }
            catch (IOException e){
                System.out.println("IOException déserialization paquet UDP");
                //e.printStackTrace();
            }
            catch (ClassNotFoundException e){
                System.out.println("IOException déserialization paquet UDP");
                //e.printStackTrace();
            }
        }
    }
}
