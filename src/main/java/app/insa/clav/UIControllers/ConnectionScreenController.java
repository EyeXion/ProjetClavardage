package app.insa.clav.UIControllers;

import app.insa.clav.Core.DataBaseAccess;
import app.insa.clav.Core.Model;
import app.insa.clav.Core.Utilisateurs;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ConnectionScreenController implements Initializable, PropertyChangeListener {

    @FXML
    private TextField loginInput;

    @FXML
    private TextField pseudoInput;

    @FXML
    private FontIcon submitButton;

    @FXML
    private ProgressIndicator spinnerSubmit;

    @FXML
    private Label errorLabel;

    @FXML
    private Model model;

    private boolean isSubmitting;

    private DataBaseAccess dbAccess;



    public ConnectionScreenController(){
        this.model = Model.getInstance();
        this.dbAccess = DataBaseAccess.getInstance();
        this.model.addPropertyChangeListener(this,"pseudoRefused");
        this.model.addPropertyChangeListener(this,"pseudoValide");
        isSubmitting = false;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    void submitConnection(ActionEvent event) {
        String pseudo = this.pseudoInput.getText();
        if (pseudo != null && !this.isSubmitting) {
            this.isSubmitting = true;
            this.spinnerSubmit.setVisible(true);
            this.model.choosePseudo(this.pseudoInput.getText());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()){
            case "pseudoValide" :
                this.isSubmitting = false;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Stage mainStage = (Stage) submitButton.getScene().getWindow();
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainWindow.fxml"));
                        try {
                            Parent rootMainScreen = fxmlLoader.load();
                            Scene scene = new Scene(rootMainScreen);
                            mainStage.setScene(scene);
                            mainStage.show();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                });
            case "pseudoRefused" :
                this.isSubmitting = false;
                Platform.runLater(() ->
                        this.errorLabel.setVisible(true)
                );
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        errorLabel.setVisible(true);
                        spinnerSubmit.setVisible(false);
                    }
                });
        }
    }
}
