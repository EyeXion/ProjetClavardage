package app.insa.clav.Core;

import app.insa.clav.Messages.Message;
import app.insa.clav.UI.UserInterface;
import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * Controleur du modèle MVC. Interface entre le modèle (Application) et l'UI.
 * Un controleur pour chaque fenêtre (main window ou fenetre de clavardage).
 * Il est instancié par le vue direcement.
 */
public class Controller implements PropertyChangeListener {

    /**
     * Application. UNE SEULE INSTANCE DANS TOUT LE PROGRAMME (faire un singleton ?)
     */
    private Model model;

    /**
     * Texte affiché au dessous du input Pseudo
     */
    @FXML
    private Label pseudoLabel;

    /**
     * Boutton pour envoyer une demande de pseudo
     */
    @FXML
    private Button sendPseudoButton;
    /**
     * ListeView qui liste les users connectés
     */
    @FXML
    private ListView listUserDisplay;

    /**
     * Input où l'utilisateur rentre son pseudo
     */
    @FXML
    private TextField pseudoInput;

    /**
     * Petit cercle de chargement pendant validation pseudo
     */
    @FXML
    private ProgressIndicator spinnerPseudo;

    private ObservableList<String> listUsers;

    /**
     * Contructeur. Il crée lui même l'UI (plus tard on mettra quel type de fenetre en argument)
     */
    public Controller(){
        this.listUsers = FXCollections.emptyObservableList();
    }

    /**
     * Permet de setup le model.
     * @param model
     */
    public void setupController(Model model){
        this.model = model;
        this.model.addPropertyChangeListener(this,"pseudoRefused");
        this.model.addPropertyChangeListener(this,"pseudoValide");
    }

    /**
     * Méthode appellée par le vue (appui boutton) pour enclencher le changement de pseudo
     * @param pseudo
     *              Pseudo rentré par l'utilisateur
     */
    public void choosePseudo(String pseudo){
       model.choosePseudo(pseudo);
    }

    /**
     * HAndler appelé par la vue quand boutton changement pseudo appuyé
     * @param evt
     */
    public void buttonPseudoHandler(ActionEvent evt){
        String newPseudo = this.pseudoInput.getText();
        if (!newPseudo.equals(this.model.user.getPseudo())){
            this.model.choosePseudo(newPseudo);
            this.spinnerPseudo.setVisible(true);
        }
    }

    /**
     * Handler de notification (Obsevateur) pour le model
     * On doit mettre les modifs de l'UI dans les Platform.runLater pour être sur de
     * changer l'UI dans le bon thread (sinon exception)
     *
     * @param evt
     *             Evenement qui est envoyé avec la notification
     */
    public void propertyChange(PropertyChangeEvent evt){
        System.out.println("Dans le property change Controller");
        switch (evt.getPropertyName()){
            case "pseudoRefused":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        pseudoLabel.setText("Pseudo refusé ! Réessayez svp.");
                        spinnerPseudo.setVisible(false);
                    }
                });
                break;
            case "pseudoValide" :
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        pseudoLabel.setText("Bienvenue, " + model.user.getPseudo() + " !");
                        spinnerPseudo.setVisible(false);
                    }
                });
                break;
        }
    }

}
