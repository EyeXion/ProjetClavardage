package app.insa.clav.Core;

import app.insa.clav.Messages.*;
import app.insa.clav.Reseau.*;
import javafx.application.Application;
import javafx.application.Platform;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.*;
//Toutes les interactions avec l'utilisateur (pour tester)

/**
 * Notre application principale. Une seule instance. représente le modèle du MVC
 */
public class Model implements PropertyChangeListener{

    /**
     * Liste des utilisateurs connectés.
     */
    private List<Utilisateurs> userList;

    private ServletConnection servCon;

    /**
     * Timer qui permet de planifier des éxécutions dans le temps
     */
    private Timer tim;
    private Timer tim2s;

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
    private boolean configure;


    /**
     * List of all TCPChatConnextions that are used
     */
    private ArrayList<TCPChatConnection> listTCPConnection;

    /**
     * Instance of TCPListener to listen for chat oppening demands
     */
    private TCPListener tcpListener;

    private DataBaseAccess dbAccess;

    private String addrBroadcast;
    private int portListening;
    private Application app;


    public Model(String addrBroadcast, int portListening, Application app, String addrBdd, String userBdd, String mdpBdd, String nomBdd, String urlServeur) {
        this.addrBroadcast = addrBroadcast;
        this.portListening = portListening;
        this.configure = false;

        try {
            this.user = new Utilisateurs("NA", InetAddress.getLocalHost(), 0, 0);
            this.tcpListener = new TCPListener(user.getId());
            this.user.setTcpListeningPort(this.tcpListener.getPort());
            this.tim= new Timer();
            this.tim2s= new Timer();
            this.support = new PropertyChangeSupport(this);
        }
        catch (IOException e){
            System.out.println("IOException dans la creation du modele");
            e.printStackTrace();
        }
        this.userList = Collections.synchronizedList(new ArrayList<>());
        this.listTCPConnection = new ArrayList<TCPChatConnection>();
        this.dbAccess = DataBaseAccess.getInstance(addrBdd, userBdd, mdpBdd, nomBdd);
        this.app = app;
        this.servCon = ServletConnection.getInstance(urlServeur);
    }

    public void configModelIndoor(){
        try {
            boolean founded = false;
            InetAddress addrBcst = InetAddress.getByName(this.addrBroadcast);
            InetAddress addrLocal = null;

            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements() && !founded) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isLoopback() && networkInterface.isUp()) {
                    List<InterfaceAddress> addressesInterface = networkInterface.getInterfaceAddresses();
                    //System.out.println(addressesInterface.toString());
                    Iterator<InterfaceAddress> addressesInterfaceIterator = addressesInterface.iterator();
                    while (addressesInterfaceIterator.hasNext()) {
                        InterfaceAddress addresseInterface = addressesInterfaceIterator.next();
                        if (addresseInterface.getBroadcast() != null && addresseInterface.getBroadcast().equals(addrBcst)) {
                            founded = true;
                            addrLocal = addresseInterface.getAddress();
                        }
                    }
                }
            }

            //System.out.println("Addresse de broadcast : " + addrBcst.toString() + "\nAddresse de l'utilisateur local : " + addrLocal.toString());

            this.user.setOutdoor(false);
            this.user.setInetAddress(addrLocal);
            this.tcpListener = new TCPListener(this.user.getInetAddress(), user.getId());
            this.user.setTcpListeningPort(this.tcpListener.getPort());
            this.UDPOut = new UDPOutput(addrBcst, this.user.getInetAddress(), this.portListening);
            this.UDPIn = new UDPInput(this.portListening);
        }
        catch (IOException e){
            System.out.println("IOException dans la creation du modele");
            e.printStackTrace();
        }

        this.openInputUDP();
        this.openTCPListener();
        this.configure = true;
    }

    public boolean isConfigured() {
        return this.configure;
    }


    /**
     * @param addrBroadcast
     *          adresse de broadcast du réseau
     * @param portListening
     *          port d'ecoute UDP
     * @param app
     *          L'application elle meme
     * @param addrBdd
     *          L'adresse de la base de donnee
     * @param userBdd
     *          L'utilisateur de la BDD
     * @param mdpBdd
     *          Le mot de passe qui lui est associé
     * @return
     */
    public static Model getInstance(String addrBroadcast, int portListening, Application app, String addrBdd, String userBdd, String mdpBdd, String nomBdd, String urlServeur){
        synchronized(Model.class){
            if (instance == null) {
                instance = new Model(addrBroadcast, portListening, app, addrBdd, userBdd, mdpBdd, nomBdd, urlServeur);
            }
        }
        return instance;
    }

    /**
     * getInstance, but no parameters (dont crete if not existing
     * @return instance of Model
     */
    public static Model getInstance(){
        if (instance == null) {
            System.out.println("ATTENTION : getInstance null renvoyé");
        }
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

    public void deletePropertyChangeListener(PropertyChangeListener pcl, String propertyName){
        this.support.removePropertyChangeListener(propertyName,pcl);
    }

    /**
     * Envoi un messagePseudo de type 1
     */
    public void sendPseudoBroadcast(){
        //System.out.println("Demande de validaton du pseudo : " + this.user.getPseudo());
        MessagePseudo msg = new MessagePseudo(1, this.user.getInetAddress(), this.user.getPseudo(), 0, this.user.getId());
        //System.out.println("Message envoyé : " + msg.toString());
        UDPOut.sendBrdcst(msg);
    }

    /**
     * Envoi message de type 4 (confirmation pseudo)
     */
    public void sendPseudoValideBroadcast(){
        //System.out.println("Confirmation du pseudo : " + this.user.getPseudo());
        MessagePseudo msg = new MessagePseudo(4, this.user.getInetAddress(), this.user.getPseudo(), this.user.getTcpListeningPort(), this.user.getId());
        //System.out.println("Message envoyé : " + msg.toString());
        UDPOut.sendBrdcst(msg);
        //UDPIn.printFilter();
    }

    /**
     * Méthode appelée par le controleur quand la vue envoie un signal d'appuis bouton changer Pseudo (indoor)
     * @param pseudo
     *              Pseudo rentré par l'utilisateur
     */
    public void choosePseudo(String pseudo, boolean isConfirmationNeeded, boolean isForConnection){
        this.ancienPseudo = this.user.getPseudo();
        this.user.setPseudo(pseudo);
        this.UDPIn.setFilterValue(2, true);
        this.UDPIn.setFilterValue(3, true);
        this.UDPIn.setFilterValue(10, true);
        this.sendPseudoBroadcast();
        this.tim.schedule(new TimerTaskResponseWait(isConfirmationNeeded), 2000);
        ArrayList<Utilisateurs> outdoorUsers = servCon.getRemoteActiveUsers();
        for (Utilisateurs newUser : outdoorUsers) {
            if (newUser.getId() == this.user.getId() && isForConnection){
                this.isPseudoOk = false;
                this.user.setPseudo(this.ancienPseudo);
                this.ancienPseudo = "";
                this.support.firePropertyChange("loginRefused", this.user.getPseudo(), this.ancienPseudo);
            }
            else if (newUser.getPseudo().equals(this.user.getPseudo())) {
                this.isPseudoOk = false;
                this.user.setPseudo(this.ancienPseudo);
                this.ancienPseudo = "";
                this.support.firePropertyChange("pseudoRefused", this.user.getPseudo(), this.ancienPseudo);
            }
            synchronized (userList) {
                if (!this.userList.contains(newUser)) {
                    this.userList.add(newUser);
                    Collections.sort(this.userList);
                    this.support.firePropertyChange("newUserConnected", -1, -2);
                }
            }
        }
    }

    /**
     * Méthode appelée par le controleur quand la vue envoie un signal d'appuis bouton changer Pseudo
     * @param pseudo
     *              Pseudo rentré par l'utilisateur
     */
    public void choosePseudoOutdoor(String pseudo, boolean isConfirmationNeeded, boolean isForConnection){
        this.ancienPseudo = this.user.getPseudo();
        this.user.setPseudo(pseudo);
        this.tim.schedule(new TimerTaskResponseWait(isConfirmationNeeded), 2000);
        ArrayList<Utilisateurs> users = servCon.getAllActiveUsers();
        for (Utilisateurs newUser : users) {
            if (newUser.getId() == this.user.getId() && isForConnection){
                this.isPseudoOk = false;
                this.user.setPseudo(this.ancienPseudo);
                this.ancienPseudo = "";
                this.support.firePropertyChange("loginRefused", this.user.getPseudo(), this.ancienPseudo);
            }
            else if (newUser.getPseudo().equals(this.user.getPseudo())) {
                this.isPseudoOk = false;
                this.user.setPseudo(this.ancienPseudo);
                this.ancienPseudo = "";
                this.support.firePropertyChange("pseudoRefused", this.user.getPseudo(), this.ancienPseudo);
            }
            synchronized (userList) {
                if ((!this.userList.contains(newUser)) && (!newUser.equals(this.user))) {
                    this.userList.add(newUser);
                    Collections.sort(this.userList);
                    this.support.firePropertyChange("newUserConnected", -1, -2);
                }
            }
        }
    }

    /** Sets the id
     * @param id
     */
    public void setUserId(int id){
        this.user.setId(id);
    }


    /** Creates a Chat Room and TCPConnection between remote id and local id when local user initiates it
     * @param remoteId
     * @param remotePseudo
     */
    public void createChatFromLocalRequest(int remoteId, String remotePseudo){
        boolean isChatAlreadyCreated = false;
        for (TCPChatConnection tcpCo : listTCPConnection){
            if (tcpCo.remoteUserId == remoteId){
                isChatAlreadyCreated = true;
                break;
            }
        }
        if (!isChatAlreadyCreated) {
            Utilisateurs remoteUser = this.getUserFromId(remoteId);
            if (remoteUser.isOutdoor() || this.user.isOutdoor()) {
                System.out.println("Creation d'un chat avec un outdoor");
                MessageInit msgInit = new MessageInit(7, user.getInetAddress(), remoteUser.getInetAddress(), remoteUser.getTcpListeningPort(), user.getId());
                if (this.servCon.SubmitConnectionChat(remoteUser.getId(), user.getId(), msgInit)) {
                    TCPChatConnection tcpCo = new TCPChatConnection(null, remoteId, this.user.getId(), null, null, null, null);
                    tcpCo.setOutdoor();
                    tcpCo.startTCPCo();
                    listTCPConnection.add(tcpCo);
                }
            } else {
                System.out.println("Creattion d'un chat avec un indoor");
                synchronized (userList) {
                    for (Utilisateurs u : userList) {
                        if (u.getPseudo().equals(remotePseudo)) {
                            MessageInit msgInit = new MessageInit(7, user.getInetAddress(), u.getInetAddress(), u.getTcpListeningPort(), user.getId());
                            TCPChatConnection tcpCo = new TCPChatConnection(msgInit, u.getInetAddress(), u.getTcpListeningPort(), u.getId());
                            tcpCo.startTCPCo();
                            listTCPConnection.add(tcpCo);
                        }
                    }
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
                //System.out.println("Message de type 2 reçu : " + msgP2.toString());
                Utilisateurs newUser2 = new Utilisateurs(msgP2.pseudo,msgP2.srcIP,msgP2.id, msgP2.portEcouteTCP);
                //System.out.println("Utilisateur créé : " + newUser2.toString());
                synchronized (userList) {
                    if (!this.userList.contains(newUser2)) {
                        this.userList.add(newUser2);
                        Collections.sort(this.userList);
                        this.support.firePropertyChange("newUserConnected", -1, -2);
                    }
                }
                break;
            case 3 :
                MessagePseudo msgP3 = (MessagePseudo) msg;
                //System.out.println("Message de type 3 reçu : " + msgP3.toString());
                this.isPseudoOk = false;
                this.user.setPseudo(this.ancienPseudo);
                this.ancienPseudo = "";
                this.support.firePropertyChange("pseudoRefused",this.user.getPseudo(),this.ancienPseudo);
                //System.out.println("After pseudo refused");
                break;
            case 4:
                MessagePseudo msgP4 = (MessagePseudo) msg;
                //System.out.println("Message de type 4 reçu : " + msgP4.toString());
                if (msgP4.id != this.user.getId()) {
                    Utilisateurs newUser4 = new Utilisateurs(msgP4.pseudo,msgP4.srcIP,msgP4.id, msgP4.portEcouteTCP);
                    newUser4.setOutdoor(false);
                    //System.out.println("Utilisateur créé : " + newUser4.toString());
                    synchronized (userList) {
                        this.userList.remove(newUser4);
                        this.userList.add(newUser4);
                        Collections.sort(this.userList);
                    }
                    this.support.firePropertyChange("newUserConnected",-1,newUser4.getId());
                }
                break;
            case 7 :
                MessagePseudo msgP7 = (MessagePseudo) msg;
                Utilisateurs User7 = new Utilisateurs(msgP7.pseudo,msgP7.srcIP,msgP7.id, msgP7.portEcouteTCP);
                synchronized (userList) {
                    this.userList.remove(User7);
                }
                this.support.firePropertyChange("newUserConnected",-1,-2);
                break;
            case 10:
                MessagePseudo msgP10 = (MessagePseudo) msg;
                //System.out.println("Message de type 10 reçu : " + msgP3.toString());
                this.isPseudoOk = false;
                this.user.setPseudo(this.ancienPseudo);
                this.ancienPseudo = "";
                this.support.firePropertyChange("loginRefused",this.user.getPseudo(),this.ancienPseudo);
                //System.out.println("After pseudo refused");
                break;
            default :
                //System.out.println("Message de type inconnu");
        }
    }

    /**
     * Méthode appelée dans messageHandler pour répondre à un message de type 1
     * @param msg
     *          Message à analyser
     */
    private void handleType1Message(MessagePseudo msg){
        MessagePseudo msgResponse;
        System.out.println("Message de type 1 reçu : " + msg.toString() + " local addr IP " + this.user.getInetAddress());
        if (!this.user.getInetAddress().equals(msg.srcIP)) {
            if ((this.user.getId() == msg.id)) {
                msgResponse = new MessagePseudo(10, this.user.getInetAddress(), this.user.getPseudo(), this.user.getTcpListeningPort(), this.user.getId());
            } else {
                if (!this.user.getPseudo().equals(msg.pseudo)) {
                    msgResponse = new MessagePseudo(2, this.user.getInetAddress(), this.user.getPseudo(), this.user.getTcpListeningPort(), this.user.getId());
                    //System.out.println("Pseudo OK, on envoi : " + msgResponse.toString());
                }
                else{
                    msgResponse = new MessagePseudo(3, null, msg.pseudo, 0, 0);
                    //System.out.println("Pseudo pas OK, on envoi : " + msgResponse.toString());

                }
            }
            this.UDPOut.sendMsg(msgResponse, msg.srcIP);
        }
    }

    /**
     * @return the list of users
     */
    public List<Utilisateurs> getUserList(){
        synchronized (userList) {
            return userList;
        }
    }

    /** returns connected the user identified by the id
     * @param id
     * @return User identified by id
     */
    public Utilisateurs getUserFromId(int id){
        Utilisateurs res = null;
        synchronized (userList) {
            for (Utilisateurs u : userList) {
                if (u.getId() == id) {
                    res = u;
                    break;
                }
            }
        }
        return res;
    }


    /** Returns connected user identified by pseudo
     * @param pseudo
     * @return User identified by pseudo
     */
    public Utilisateurs getUserFromPseudo(String pseudo){
        Utilisateurs res = null;
        synchronized (userList) {
            for (Utilisateurs u : userList) {
                if (u.getPseudo().equals(pseudo)) {
                    res = u;
                    break;
                }
            }
        }
        return res;
    }

    /**
     * Sends a deconnection Messages (type 7) in broadcast
     */
    public void sendDeconnectionMessage() {
        if (!this.user.isOutdoor()) {
            MessagePseudo msg = new MessagePseudo(7, this.user.getInetAddress(), this.user.getPseudo(), this.user.getTcpListeningPort(), this.user.getId());
            UDPOut.sendBrdcst(msg);
            servCon.submitDeconnectionIndoor(this.user);
        }
        else{
            //System.out.println("Send deco Outdoor sent");
            servCon.submitDeconnectionOutdoor(this.user);
        }
        try {
            this.app.stop();
            Platform.exit();
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Erreur lors de l'envoi du message de deconnexion'");
        }

    }

    /** Closes the Chat Connection when asked
     * @param tcpCo
     */
    public void notifyCloseChat(TCPChatConnection tcpCo) {
        this.listTCPConnection.remove(tcpCo);
        if (!tcpCo.isOutdoor()) {
            Socket link = tcpCo.getSocket();
            try {
                link.close();
            } catch (IOException e) {
                System.out.println("Erreur lors de la fermeture du Chat");
            }
        }
    }

    public void deleteHistory(String remotePseudo) {
        int remoteId = this.getUserFromPseudo(remotePseudo).getId();
        for (TCPChatConnection tcpCo : this.listTCPConnection){
            if (tcpCo.remoteUserId == remoteId){
                this.listTCPConnection.remove(tcpCo);
                tcpCo.sendCloseChat();
                Socket link = tcpCo.getSocket();
                try {
                    link.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        this.dbAccess.deleteHistory(remoteId,user.getId());
    }

    public void startTim2s() {
        tim2s.scheduleAtFixedRate(new TimerTaskCheckUsers(), 2000, 2000);
    }


    public void recupAndHandleConnexion() {
        ArrayList<MessageInit> messages = this.servCon.GetConnectionChat(this.user.getId());
        if (messages != null) {
            for (Iterator<MessageInit> iter = messages.iterator(); iter.hasNext(); ) {
                MessageInit msg = iter.next();
                TCPChatConnection newCo = new TCPChatConnection(null, msg.id, this.user.getId(), null, null, null, null);
                newCo.setOutdoor();
                newCo.startTCPCo();
                this.listTCPConnection.add(newCo);
            }
        }
    }


    /**
     * Classe interne au model pour au bout d'une seconde d'envoi de demande pseudo type 1,
     * on desactive les filtes et on met à jour la vue.
     */
    class TimerTaskResponseWait extends TimerTask {

        boolean isConfirmationNeeded;

        /**
         * @param isConfirmationNeeded if true, will send type 4 messages in broadcast else does not send
         */
        public TimerTaskResponseWait(boolean isConfirmationNeeded) {
            this.isConfirmationNeeded = isConfirmationNeeded;
        }

        /**
         * Quand la seconde s'est écoulée, on met les filtres à faux pour ne plus prendre en compte les messages de type 2 et 3
         */
        public void run() {
            if (!user.isOutdoor()) {
                UDPIn.setFilterValue(2, false);
                UDPIn.setFilterValue(3, false);
                UDPIn.setFilterValue(10, false);
            }
            if (isPseudoOk){
                //envoi message de type 4 pour confirmer.
                support.firePropertyChange("pseudoValide",ancienPseudo,user.getPseudo());
                if (!user.isOutdoor()) {
                    UDPIn.setFilterValue(1,true);
                }
                if (isConfirmationNeeded){
                    if (!user.isOutdoor()) {
                        sendPseudoValideBroadcast();
                        servCon.submitConnectionIndoor(user);
                    }
                    else{
                        servCon.submitConnectionOutdoor(user);
                    }
                }
            }
            else{
                isPseudoOk = true; //On reinitialise cet attribut.
            }
        }
    }

    /**
     * Classe interne au model check si tout les utilisateurs sont toujours présents
     */
    class TimerTaskCheckUsers extends TimerTask {
        public void run() {
            //System.out.println(user);
            user.update();
            synchronized (userList) {
                Date currentDate = new Date();
                if (user.isOutdoor()) {
                    System.out.println("Timer task 2s Outdoor");
                    servCon.submitConnectionOutdoor(user);
                    ArrayList<Utilisateurs> servUserList = servCon.getAllActiveUsers();
                    ArrayList<Utilisateurs> userListToRemove = new ArrayList<>();
                    ArrayList<Utilisateurs> userListToAdd = new ArrayList<>();
                    ArrayList<Utilisateurs> userListWatched = new ArrayList<>();
                    for (Iterator<Utilisateurs> iter = userList.iterator(); iter.hasNext(); ) {
                        Utilisateurs userloop = iter.next();
                        Utilisateurs userfound = null;
                        for (Utilisateurs userExtloop : servUserList) {
                            if (userExtloop.getId() == userloop.getId()) {
                                userfound = userExtloop;
                                break;
                            }
                        }
                        if (userfound == null) {
                            userListToRemove.add(userloop);
                        } else {
                            userListWatched.add(userfound);
                            if (userfound.userToOld(currentDate)) {
                                userListToRemove.add(userloop);
                                if (userfound.isOutdoor()) {
                                    servCon.submitDeconnectionOutdoor(userfound);
                                } else {
                                    servCon.submitDeconnectionIndoor(userfound);
                                }
                            } else {
                                userListToRemove.add(userloop);
                                userListToAdd.add(userfound);
                            }
                        }
                    }
                    userList.removeAll(userListToRemove);
                    servUserList.removeAll(userListWatched);
                    if (servUserList.remove(user)) {
                        System.out.println(("\n\nUSER SUPP\n\n"));
                    }
                    userListToAdd.addAll(servUserList);
                    userList.addAll(userListToAdd);
                } else {
                    // On previent qu'on existe
                    System.out.println("Timer task 2s Indoor");
                    System.out.println("UserList " + userList.toString());
                    sendPseudoValideBroadcast();
                    servCon.submitConnectionIndoor(user);

                    ArrayList<Utilisateurs> utilisateursExternes = servCon.getRemoteActiveUsers();
                    ArrayList<Utilisateurs> userListToRemove = new ArrayList<>();
                    ArrayList<Utilisateurs> userListToAdd = new ArrayList<>();
                    ArrayList<Utilisateurs> userListExternWatched = new ArrayList<>();
                    for (Iterator<Utilisateurs> iter = userList.iterator(); iter.hasNext(); ) {
                        Utilisateurs userloop = iter.next();
                        if (userloop.isOutdoor()) {
                            Utilisateurs userfound = null;
                            for (Utilisateurs userExtloop : utilisateursExternes) {
                                if (userExtloop.getId() == userloop.getId()) {
                                    userfound = userExtloop;
                                    break;
                                }
                            }
                            if (userfound == null) {
                                userListToRemove.add(userloop);
                            } else {
                                userListExternWatched.add(userfound);
                                if (userfound.userToOld(currentDate)) {
                                    userListToRemove.add(userloop);
                                    servCon.submitDeconnectionOutdoor(userfound);
                                } else {
                                    userListToRemove.add(userloop);
                                    userListToAdd.add(userfound);
                                }
                            }
                        } else {
                            if (userloop.userToOld(currentDate)) {
                                userListToRemove.add(userloop);
                                servCon.submitDeconnectionIndoor(userloop);
                            }
                        }
                    }
                    userList.removeAll(userListToRemove);
                    utilisateursExternes.removeAll(userListExternWatched);
                    userListToAdd.addAll(utilisateursExternes);
                    userList.addAll(userListToAdd);
                }
            }

            Collections.sort(userList);
            support.firePropertyChange("newUserConnected",-1,-2);

            recupAndHandleConnexion();
        }
    }

    /** if (user.isOutdoor()) {
     userList = servCon.getAllActiveUsers();
     } else {
     ArrayList<Utilisateurs> userListToRemove = new ArrayList<>();
     for (Iterator<Utilisateurs> iter = userList.iterator(); iter.hasNext(); ) {
     Utilisateurs userloop = iter.next();
     if (userloop.isOutdoor()) {
     //System.out.println("OUAIP CA DECNNE");
     userListToRemove.add(userloop);
     }
     }
     //System.out.println(userListToRemove);
     userList.removeAll(userListToRemove);
     userList.addAll(servCon.getRemoteActiveUsers());
     }
     support.firePropertyChange("newUserConnected",-1,-2);
     *
     *
     *
     *
     *
     *
     *
     *
*/
}

