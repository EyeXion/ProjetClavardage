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

public class PseudoWindowController implements PropertyChangeListener, Initializable {

    @FXML
    private TextField pseudoInput;

    @FXML
    private JFXButton buttonValidatePseudo;

    @FXML
    private Label errorLabel;

    @FXML
    private ProgressIndicator spinnerPseudo;

    private Model model;


    public PseudoWindowController(){
        this.model = Model.getInstance();
        this.model.addPropertyChangeListener(this,"pseudoRefused");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }


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

    public void clickButtonValidatePseudo(ActionEvent evt){
        String newPseudo = this.pseudoInput.getText();
        if (!newPseudo.equals(this.model.user.getPseudo())){
            this.model.choosePseudo(newPseudo);
            this.spinnerPseudo.setVisible(true);
        }
    }

}
