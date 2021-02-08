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
     * nport d'ecoute TCP
     */
    public int portEcouteTCP;


    /**
     * Créé un message à partir des info.
     * @see Message
     * @param typeMessage
     * @param srcIP
     * @param pseudo
     * @param portEcouteTCP
     * @param id
     */
    public MessagePseudo(int typeMessage, InetAddress srcIP, String pseudo, int portEcouteTCP, int id) {
        super(typeMessage, srcIP);
        this.pseudo = pseudo;
        this.portEcouteTCP = portEcouteTCP;
        this.id = id;
    }

    @Override
    public String toString() {
        return "MessagePseudo{" +
                "typeMessage=" + typeMessage +
                ", srcIP=" + srcIP +
                ", pseudo='" + pseudo + '\'' +
                ", id=" + id +
                ", portEcouteTCP=" + portEcouteTCP +
                "} " + super.toString();
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
