package app.insa.clav.Messages;

import java.io.Serializable;
import java.net.InetAddress;

//Classe de base de tout les messages. Les payloads seront précisés dans les classes filles

/* typeMessage Number :
    1 ---> Messages.MessagePseudo envoi en 1er
    2 ---> Messages.MessagePseudo réponse à type 1 pseudo ok
    3 ---> Messages.MessagePseudo réponse à type 1 pseudo pas ok
    4 ---> Messages.MessagePseudo si après type 1 aucun type 2 pour un delai, envoi confirmation pseudo
 */

/**
 * Message de base, classe mère dont les message plus spécifiques héritent.
 */
public class Message implements Serializable {

    //private static final long serialVersionUID = 7526472295622776147L;

    /**
     * Type de message. </br>
     * 1 -> Demande de validation de pseudo (MessagePseudo) </br>
     * 2 -> Réponse validation pseudo ok (MessagePseudo) </br>
     * 3 -> Réponse validation pseudo pas ok (MessagePseudo)
     */
    public int typeMessage;
    public InetAddress srcIP;
    public int srcResponsePort; //Seulement besoin car on teste en localHost
    public InetAddress destIP;
    public int destPort;

    /**
     * Constructeur d'un message depuis un autre
     * @param msg
     */
    public Message(Message msg){
        this.typeMessage = msg.typeMessage;
        this.srcIP = msg.srcIP;
        this.srcResponsePort = msg.srcResponsePort;
        this.destIP = msg.destIP;
        this.destPort = msg.destPort;
    }

    /**
     * Construit un message depuis les infos en paramètre
     * @param typeMessage
     *                  Type du message.
     *                  Voir autres messages pour précisions
     *
     * @param srcIP
     *              IP depuis laquelle le msg à été envoyé
     * @param srcResponsePort
     *              Port à utiliser pour répondre à ce message
     * @param destIP
     *              Ip de la machine destinataire
     * @param destPort
     *              Port de la machine destinataire
     */
    public Message(int typeMessage,InetAddress srcIP, int srcResponsePort,InetAddress destIP,int destPort){
        this.typeMessage = typeMessage;
        this.srcIP = srcIP;
        this.srcResponsePort = srcResponsePort;
        this.destIP = destIP;
        this.destPort = destPort;
    }
}
