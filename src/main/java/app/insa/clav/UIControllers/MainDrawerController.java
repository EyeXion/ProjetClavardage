package app.insa.clav.UIControllers;

import app.insa.clav.Core.Model;
import app.insa.clav.UISubStages.PseudoStage;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller of the drawer of the main Window
 */
public class MainDrawerController implements PropertyChangeListener,Initializable {

    /**
     * Application. UNE SEULE INSTANCE DANS TOUT LE PROGRAMME (faire un singleton ?)
     */
    private Model model;

    /**
     * boutton de demande ouverture fenetre changement pseudo
     */
    @FXML
    private JFXButton buttonPseudo;
    /**
     * fenetre de changemen de pseudo
     */
    private PseudoStage pseudoWindow;


    /**
     * constructor. Gets the Model
     */
    public MainDrawerController(){
        this.model = Model.getInstance();
        this.model.addPropertyChangeListener(this,"pseudoValide");
    }


    /**
     * Called by the loader of FXML files
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * HAndler appelé par la vue quand boutton changement pseudo appuyé
     * @param evt
     */
    public void buttonPseudoHandler(ActionEvent evt){
        this.pseudoWindow = new PseudoStage(this.model);
    }

    /**
     * Ecoute les signaux qui viennent du Model
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()){
            case "pseudoValide" :
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        pseudoWindow.close();
                    }
                });
                break;
        }
    }
}
