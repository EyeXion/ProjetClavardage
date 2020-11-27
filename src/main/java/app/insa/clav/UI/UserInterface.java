package app.insa.clav.UI;

import app.insa.clav.Core.Controller;

/**
 * UI (test pour l'instant)
 */
public class UserInterface {

    /**
     * Controlleur lié à cette fenetre
     */
    private Controller ctrl;

    /**
     * @param pseudo
     *              Fonction liée au boutton d'envoi de pseudo (par forcement utile,
     *              on peut lier directement la fonction du controleur au bouton en vrai)
     */
    public void pressButtonChangePseudo(String pseudo){
        this.ctrl.choosePseudo(pseudo);
    }

    /** Pour les tests. Print un message.
     * @param payload
     */
    public void printMessagePseudo(String payload){
        System.out.println(payload);
    }

}
