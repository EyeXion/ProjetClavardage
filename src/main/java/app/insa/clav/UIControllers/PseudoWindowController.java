package app.insa.clav.UIControllers;

import app.insa.clav.Core.Model;
import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

/**
 * Controller de la fenetre de changement de pseudo
 */
public class PseudoWindowController implements PropertyChangeListener, Initializable {

    /**
     * Input pour l'utilisateur
     */
    @FXML
    private TextField pseudoInput;

    /**
     * boutton qui permet à l'utilisateur d'envoyer le formulaire
     */
    @FXML
    private JFXButton buttonValidatePseudo;

    /**
     * text affiché si pseudo non valide
     */
    @FXML
    private Label errorLabel;

    /**
     * Petit spninner pour indiquer le chargement
     */
    @FXML
    private ProgressIndicator spinnerPseudo;

    /**
     * Instance du model
     */
    private final Model model;


    /**
     * Constructeur
     */
    public PseudoWindowController(){
        this.model = Model.getInstance();
        this.model.addPropertyChangeListener(this,"pseudoRefused");
    }

    /**
     * Appelé par le loader FXML
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }


    /**
     * Ecoute les singaux du model
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "pseudoRefused":
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        errorLabel.setVisible(true);
                        spinnerPseudo.setVisible(false);
                    }
                });
                break;
        }
    }

    /**
     * handler click sur le bouton d'envoi
     * @param evt
     */
    public void clickButtonValidatePseudo(ActionEvent evt){
        String newPseudo = this.pseudoInput.getText();
        if (!newPseudo.equals(this.model.user.getPseudo())){
            if (!this.model.user.isOutdoor()) {
                this.model.choosePseudo(newPseudo, true,false);
            } else {
                this.model.choosePseudoOutdoor(newPseudo, true,false);
            }
            this.spinnerPseudo.setVisible(true);
        }
    }

}
