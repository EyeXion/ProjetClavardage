package app.insa.clav.Messages;

import java.net.InetAddress;

//Messages.Message qui permet d'envoyer un pseudo. typeMessage = 1


/**
 * Classe de message pour envoyer des truc concernant les Pseudo/ les utilisateurs connéctés
 */
public class MessagePseudo extends Message {

    /**
     * Pseudo soit en demande de validation (type 1) ou alors pseudo effectif de la source du message
     */
    public String pseudo;
    /**
     * id de l'utilidateur source du message
     */
    public int id;

    /**
     * Créé un message à partir des info.
     * @see Message
     * @param typeMessage
     * @param srcIP
     * @param srcPort
     * @param destIP
     * @param destPort
     * @param pseudo
     * @param id
     */
    public MessagePseudo(int typeMessage, InetAddress srcIP, int srcPort, InetAddress destIP, int destPort, String pseudo, int id) {
        super(typeMessage, srcIP, srcPort, destIP, destPort);
        this.pseudo = pseudo;
        this.id = id;
    }

    /**
     * Crée un message à partir d'un message de base
     * @see Message
     * @param msg
     * @param id
     * @param pseudo
     */
    //Create Messages.MessagePseudo from Messages.Message
    public MessagePseudo(Message msg, int id, String pseudo){
        super(msg);
        this.pseudo = pseudo;
        this.id = id;
    }

    /**
     * permet de renvoyer un string représtant ce message (surout pour les tests)
     * @param id
     * @param pseudo
     * @return
     */
    //Returns payload for a pseudo message
    public static String pseudoPayload(int id, String pseudo){
        return Integer.toString(id) + "|" + pseudo;
    }
}
