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
    10 ---> Message.MessagePseudo réponse à type 1 login déjà connecté
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

    /**
     * Constructeur d'un message depuis un autre
     * @param msg
     */
    public Message(Message msg){
        this.typeMessage = msg.typeMessage;
        this.srcIP = msg.srcIP;
    }

    /**
     * Construit un message depuis les infos en paramètre
     * @param typeMessage
     *                  Type du message.
     *                  Voir autres messages pour précisions
     *
     * @param srcIP
     *              IP depuis laquelle le msg à été envoyé
     */
    public Message(int typeMessage,InetAddress srcIP){
        this.typeMessage = typeMessage;
        this.srcIP = srcIP;
    }
}
