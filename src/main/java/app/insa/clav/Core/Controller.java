package app.insa.clav.Core;

import app.insa.clav.UI.UserInterface;

/**
 * Controleur du modèle MVC. Interface entre le modèle (Application) et l'UI.
 * Un controleur pour chaque fenêtre (main window ou fenetre de clavardage)
 */
public class Controller {

    /**
     * Application. UNE SEULE INSTANCE DANS TOUT LE PROGRAMME (faire un singleton ?)
     */
    private Model model;
    /**
     * Fenetre liée à ce controleur
     */
    private UserInterface ui;

    /**
     * Contructeur. Il crée lui même l'UI (plus tard on mettra quel type de fenetre en argument)
     * @param model
     *             Reference vers l'application (le modèle)
     */
    public Controller(Model model){
        this.model = model;
        this.ui = new UserInterface();
    }


    public Controller(){}

    /**
     * Méthode appellée par le vue (appui boutton) pour enclencher le changement de pseudo
     * @param pseudo
     *              Pseudo rentré par l'utilisateur
     */
    public void choosePseudo(String pseudo){
       model.choosePseudo(pseudo);
    }

    /**
     * Sert pour les tests et simuler l'UI
     * @param payload
     */
    public void printMessageReceived(String payload){
        this.ui.printMessagePseudo(payload);
    }

}
