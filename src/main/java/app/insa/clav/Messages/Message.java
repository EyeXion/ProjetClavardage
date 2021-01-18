package app.insa.clav.Messages;

import java.io.Serializable;
import java.net.InetAddress;

//Classe de base de tout les messages. Les payloads seront précisés dans les classes filles

/* typeMessage Number :
    1 ---> Messages.MessagePseudo envoi en 1er
    2 ---> Messages.MessagePseudo réponse à type 1 pseudo ok
    3 ---> Messages.MessagePseudo réponse à type 1 pseudo pas ok
    4 ---> Messages.MessagePseudo si après type 1 aucun type 2 pour un delai, envoi confirmation pseudo
    5 ---> Message Init envoyé quand on se connecte en TCP à un user distant
    6 ---> MessageChatTxt pour le chat
    7 ---> Message.MessagePseudo de deconnexion
    8 ---> Message fermeture fenetre chat
    9 ---> MessageChatFile to say a file is going to be sent
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
    public InetAddress destIP; //Seulement pour UDP ou ouverture CO TCP
    public int destPort; //Seulement pour UDP ou l'ouverture connexion TCP, sinon à 0.

    /**
     * Constructeur d'un message depuis un autre
     * @param msg
     */
    public Message(Message msg){
        this.typeMessage = msg.typeMessage;
        this.srcIP = msg.srcIP;
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
     * @param destIP
     *              Ip de la machine destinataire
     * @param destPort
     *              Port de la machine destinataire
     */
    public Message(int typeMessage,InetAddress srcIP,InetAddress destIP,int destPort){
        this.typeMessage = typeMessage;
        this.srcIP = srcIP;
        this.destIP = destIP;
        this.destPort = destPort;
    }
}
