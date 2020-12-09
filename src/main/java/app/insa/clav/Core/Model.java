package app.insa.clav.Core;

import app.insa.clav.Messages.Message;
import app.insa.clav.Messages.MessageChatTxt;
import app.insa.clav.Messages.MessageInit;
import app.insa.clav.Messages.MessagePseudo;
import app.insa.clav.Reseau.TCPChatConnection;
import app.insa.clav.Reseau.TCPListener;
import app.insa.clav.Reseau.UDPInput;
import app.insa.clav.Reseau.UDPOutput;
import javafx.application.Application;
import javafx.application.Platform;
import jdk.jshell.execution.Util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.net.*;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
//Toutes les interactions avec l'utilisateur (pour tester)

/**
 * Notre application principale. Une seule instance. représente le modèle du MVC
 */
public class Model implements PropertyChangeListener{

    /**
     * Liste des utilisateurs connectés.
     */
    private ArrayList<Utilisateurs> userList;
    /**
     * Timer qui permet de planifier des éxécutions dans le temps
     */
    private Timer tim;

    /**
     * Notre interface UDP pour envoyer des messages
     */
    private UDPOutput UDPOut;
    /**
     * Interface UDP qui notifie quand un message est envoyé (observable)
     */
    private UDPInput UDPIn;
    /**
     * Infos sur notre utilisateur local
     */
    public Utilisateurs user;
    /**
     * Sert à stocker le pseudo de base quand on veut change de pseudo. Si la demande de pseudo est refusée, on revient à ce pseudo
     */
    private String ancienPseudo = "";

    /**
     * Observable, pour envoyer des notifications aux controleurs
     */
    private PropertyChangeSupport support;

    /**
     * Instance of the Model (Singleton Design Pattern)
     */
    private static Model instance = null;


    /**
     * Vrai si pseudo ok, faux sinon
     */
    private boolean isPseudoOk = true;


    private ArrayList<TCPChatConnection> listTCPConnection;

    private TCPListener tcpListener;

    private DataBaseAccess dbAccess;

    private Application app;


    /**
     * Constructeur

     *           Id de l'utilisateur (unique dans toutes les machines)
     * @param inputPort
     *                  Port d'input UDP
     * @param outputPort
     *                  Port d'Output UDP
     */
/*
ID 1 -> Listening on 6000, sending on 5000, tcpServer on 7000
ID 2 -> Listening on 6001, sending on 5001, tcpServer on 7001
ID 2 -> Listening on 6002, sending on 5002, tcpServer on 7002
*/
    private Model(int inputPort, int outputPort, int tcpListenerPort, Application app){
        try {
            this.user = new Utilisateurs("NA", InetAddress.getLocalHost(), 0, inputPort);
            this.UDPOut = new UDPOutput(InetAddress.getLocalHost(), outputPort);
            this.UDPIn = new UDPInput(user.getInetAddress(),inputPort);
            this.tcpListener = new TCPListener(this.user.getInetAddress(),tcpListenerPort,user.getId());
            this.tim= new Timer();
            this.support = new PropertyChangeSupport(this);
        }
        catch (IOException e){
            System.out.println("IOException dans la creation de l'utilisateur local");
            e.printStackTrace();
        }
        this.userList = new ArrayList<Utilisateurs>();
        this.listTCPConnection = new ArrayList<TCPChatConnection>();
        this.dbAccess = DataBaseAccess.getInstance();
        this.app = app;
    }

    /**
     * Returns the instance of the Model (and creates it if null)
     *          id of user
     * @param inputPort
     *                  input Port UDP
     * @param outputPort
     *                  outPutPort UDP
     * @return
     *         instance of Model
     */
    public static Model getInstance(int inputPort, int outputPort, int tcpListenerPort, Application app){
        synchronized(Model.class){
            if (instance == null) {
                instance = new Model(inputPort, outputPort,tcpListenerPort, app);
            }
        }
        return instance;
    }

    /**
     * getInstance, but no parameters (dont crete if not existing
     * @return instance of Model
     */
    public static Model getInstance(){
        return instance;
    }

    /**
     * Ouvre le thread d'écoute en UDP et ajoute l'application en observateur de ce thread
     */
    public void openInputUDP(){
        UDPIn.start();
        this.UDPIn.addPropertyChangeListener(this);
    }

    public void openTCPListener(){
        tcpListener.start();
        this.tcpListener.addPropertyChangeListener(this);
    }


    /**
     * permet de sauvegarder un socket TCP clanvardage qui vient d'être créé
     * @param co
     */
    public void addTCPConnexion(TCPChatConnection co){
        this.listTCPConnection.add(co);
        co.addPropertyChangeListener(this);
    }

    /**
     * Ajout un listener avec un nom de propriété.
     *
     * Liste des propriete :
     *
     *  "pseudoRefused" -> Demande de nouveau pseudo refusée
     *  "userListUpdated" -> La liste des utilisateurs connectés à changé
     *  "pseudoAccepted" -> nouveau pseudo accepte
     *
     *
     * @param pcl
     *             Listener du controller qui a appelé la methode
     * @param propertyName
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl, String propertyName){
        this.support.addPropertyChangeListener(propertyName, pcl);
    }

    /**
     * Envoi un messagePseudo de type 1 aux 3 machines de test
     */
    public void sendPseudoBroadcast(){
        try {
            if (user.getId() == 1 || user.getId() == 2) {
                MessagePseudo msg = new MessagePseudo(1, this.user.getInetAddress(), this.user.getPort(), InetAddress.getLocalHost(), 6002, this.user.getPseudo(),this.user.getId());
                UDPOut.sendMsg(msg);
            }
            if (user.getId() == 2 || user.getId() == 3) {
                MessagePseudo msg = new MessagePseudo(1, this.user.getInetAddress(), this.user.getPort(),  InetAddress.getLocalHost(), 6000, this.user.getPseudo(),this.user.getId());
                UDPOut.sendMsg(msg);
            }
            if (user.getId() == 1 || user.getId() == 3) {
                MessagePseudo msg = new MessagePseudo(1, this.user.getInetAddress(), this.user.getPort(),  InetAddress.getLocalHost(), 6001,this.user.getPseudo(),this.user.getId());
                UDPOut.sendMsg(msg);
            }
        }
        catch (UnknownHostException e){
            System.out.println(("exception Trouver host dans sendPseudoBroadcast"));
            e.printStackTrace();
        }
    }

    /**
     * Envoi message de type 4 (confirmation pseudo)
     */
    public void sendPseudoValideBroadcast(){
        try {
            if (user.getId() == 1 || user.getId() == 2) {
                MessagePseudo msg = new MessagePseudo(4, this.user.getInetAddress(), this.user.getPort(), InetAddress.getLocalHost(), 6002, this.user.getPseudo(),this.user.getId());
                UDPOut.sendMsg(msg);
            }
            if (user.getId() == 2 || user.getId() == 3) {
                MessagePseudo msg = new MessagePseudo(4, this.user.getInetAddress(), this.user.getPort(),  InetAddress.getLocalHost(), 6000, this.user.getPseudo(),this.user.getId());
                UDPOut.sendMsg(msg);
            }
            if (user.getId() == 1 || user.getId() == 3) {
                MessagePseudo msg = new MessagePseudo(4, this.user.getInetAddress(), this.user.getPort(),  InetAddress.getLocalHost(), 6001,this.user.getPseudo(),this.user.getId());
                UDPOut.sendMsg(msg);
            }
        }
        catch (UnknownHostException e){
            System.out.println(("exception Trouver host dans sendPseudoBroadcast"));
            e.printStackTrace();
        }
    }

    /**
     * Méthode appelée par le controleur quand la vue envoie un signal d'appuis bouton changer Pseudo
     * @param pseudo
     *              Pseudo rentré par l'utilisateur
     * @return
     */
    public boolean choosePseudo(String pseudo, boolean isConfirmationNeeded){
        this.ancienPseudo = this.user.getPseudo();
        this.user.setPseudo(pseudo);
        this.UDPIn.setFilterValue(2,true);
        this.UDPIn.setFilterValue(3,true);
        this.sendPseudoBroadcast();
        this.tim.schedule(new TimerTaskResponseWait(isConfirmationNeeded),1000);
        return true;
    }

    public void setUserId(int id){
        this.user.setId(id);
    }


    public void createChatFromLocalRequest(int remoteId, String remotePseudo){
        boolean isChatAlreadyCreated = false;
        for (TCPChatConnection tcpCo : listTCPConnection){
            if (tcpCo.remoteUserId == remoteId){
                isChatAlreadyCreated = true;
                break;
            }
        }
        if (!isChatAlreadyCreated) {
            for (Utilisateurs u : userList) {
                if (u.getPseudo().equals(remotePseudo)) {
                    int destPort;
                    if (u.getId() == 1) {
                        destPort = 7000;
                    } else if (u.getId() == 2) {
                        destPort = 7001;
                    } else {
                        destPort = 7002;
                    }
                    MessageInit msgInit = new MessageInit(7, user.getInetAddress(), user.getPort(), u.getInetAddress(), destPort, user.getId());
                    TCPChatConnection tcpCo = new TCPChatConnection(msgInit, u.getId());
                    listTCPConnection.add(tcpCo);
                }
            }
        }
    }

    /**
     * Handler de notification (Obsevateur) pour le thread UDP.
     * @param evt
     *             Evenement qui est envoyé avec la notification
     */
    public void propertyChange(PropertyChangeEvent evt){
        switch (evt.getPropertyName()){
            case "UDPInput":
                Message msgReceived = UDPIn.getMessageReceived();
                this.messageHandler(msgReceived);
                break;
            case "chatCreated" :
                TCPChatConnection tcpCo = this.tcpListener.getTCPChatConnection();
                tcpCo.addPropertyChangeListener(this);
                this.listTCPConnection.add(tcpCo);
                break;
        }
    }

    /**
     * Permet de faire les actions adéquates selon le type de message reçu. Appelé dans propertyChange
     * @param msg
     *          message à analyser
     */
    public void messageHandler(Message msg){
        switch (msg.typeMessage){
            case 1 :
                MessagePseudo msgP1 = (MessagePseudo) msg;
                this.handleType1Message(msgP1);
                break;
            case 2 :
                MessagePseudo msgP2 = (MessagePseudo) msg;
                Utilisateurs newUser2 = new Utilisateurs(msgP2.pseudo,msgP2.srcIP,msgP2.id,msgP2.srcResponsePort);
                if (!this.userList.contains(newUser2)) {
                    this.userList.add(newUser2);
                    Collections.sort(this.userList);
                    this.support.firePropertyChange("newUserConnected",true,false);
                }
                break;
            case 3 :
                MessagePseudo msgP3 = (MessagePseudo) msg;
                Utilisateurs newUser3 = new Utilisateurs(msgP3.pseudo,msgP3.srcIP,msgP3.id,msgP3.srcResponsePort);
                if (!this.userList.contains(newUser3)) {
                    this.userList.add(newUser3);
                    Collections.sort(this.userList);
                    this.support.firePropertyChange("newUserConnected",true,false);
                }
                this.isPseudoOk = false;
                this.support.firePropertyChange("pseudoRefused",this.user.getPseudo(),this.ancienPseudo);
                this.user.setPseudo(this.ancienPseudo);
                break;
            case 4:
                MessagePseudo msgP4 = (MessagePseudo) msg;
                Utilisateurs newUser4 = new Utilisateurs(msgP4.pseudo,msgP4.srcIP,msgP4.id,msgP4.srcResponsePort);
                this.userList.remove(newUser4);
                this.userList.add(newUser4);
                Collections.sort(this.userList);
                this.support.firePropertyChange("newUserConnected",true,false);
                break;
            case 7 :
                MessagePseudo msgP7 = (MessagePseudo) msg;
                Utilisateurs User7 = new Utilisateurs(msgP7.pseudo,msgP7.srcIP,msgP7.id,msgP7.srcResponsePort);
                this.userList.remove(User7);
                this.support.firePropertyChange("newUserConnected",true,false);
            default :
                System.out.println("Message de type inconnu");
        }
    }

    /**
     * Méthode appelée dans messageHandler pour répondre à un message de type 1
     * @param msg
     *          Message à analyser
     */
    private void handleType1Message(MessagePseudo msg){
        MessagePseudo msgResponse;
        if (this.user.getPseudo().equals(msg.pseudo)){
            msgResponse = new MessagePseudo(3, this.user.getInetAddress(), this.user.getPort(),  msg.srcIP, msg.srcResponsePort,this.user.getPseudo(),this.user.getId());
        }
        else{
            msgResponse = new MessagePseudo(2, this.user.getInetAddress(), this.user.getPort(),  msg.srcIP, msg.srcResponsePort,this.user.getPseudo(),this.user.getId());
        }
        this.UDPOut.sendMsg(msgResponse);
    }

    /**
     * @return the list of users
     */
    public ArrayList<Utilisateurs> getUserList(){
        return userList;
    }

    public Utilisateurs getUserFromId(int id){
        Utilisateurs res = null;
        for (Utilisateurs u : userList){
            if (u.getId() == id){
                res = u;
                break;
            }
        }
        return res;
    }


    public Utilisateurs getUserFromPseudo(String pseudo){
        Utilisateurs res = null;
        for (Utilisateurs u : userList){
            if (u.getPseudo().equals(pseudo)){
                res = u;
                break;
            }
        }
        return res;
    }

    public void sendDeconnectionMessage() {
        try {
            if (user.getId() == 1 || user.getId() == 2) {
                MessagePseudo msg = new MessagePseudo(7, this.user.getInetAddress(), this.user.getPort(), InetAddress.getLocalHost(), 6002, this.user.getPseudo(),this.user.getId());
                UDPOut.sendMsg(msg);
            }
            if (user.getId() == 2 || user.getId() == 3) {
                MessagePseudo msg = new MessagePseudo(7, this.user.getInetAddress(), this.user.getPort(),  InetAddress.getLocalHost(), 6000, this.user.getPseudo(),this.user.getId());
                UDPOut.sendMsg(msg);
            }
            if (user.getId() == 1 || user.getId() == 3) {
                MessagePseudo msg = new MessagePseudo(7, this.user.getInetAddress(), this.user.getPort(),  InetAddress.getLocalHost(), 6001,this.user.getPseudo(),this.user.getId());
                UDPOut.sendMsg(msg);
            }
        }
        catch (UnknownHostException e){
            System.out.println(("exception Trouver host dans sendPseudoBroadcast"));
            e.printStackTrace();
        }
        try {
            this.app.stop();
            Platform.exit();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyCloseChat(TCPChatConnection tcpCo) {
        this.listTCPConnection.remove(tcpCo);
        Socket link = tcpCo.getSocket();
        try {
            link.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Classe interne au model pour au bout d'une seconde d'envoi de demande pseudo type 1,
     * on desactive les filtes et on met à jour la vue.
     */
    class TimerTaskResponseWait extends TimerTask {

        boolean isConfirmationNeeded;
        public TimerTaskResponseWait(boolean isConfirmationNeeded) {
            this.isConfirmationNeeded = isConfirmationNeeded;
        }

        /**
         * Quand la seconde s'est écoulée, on met les filtres à faux pour ne plus prendre en compte les messages de type 2 et 3
         */
        public void run() {
            UDPIn.setFilterValue(2, false);
            UDPIn.setFilterValue(2, false);
            if (isPseudoOk){
                //envoi message de type 4 pour confirmer.
                support.firePropertyChange("pseudoValide",ancienPseudo,user.getPseudo());
                UDPIn.setFilterValue(1,true);
                if (isConfirmationNeeded){
                    sendPseudoValideBroadcast();
                }
            }
            else{
                isPseudoOk = true; //On reinitialise cet attribut.
            }
        }
    }
}
