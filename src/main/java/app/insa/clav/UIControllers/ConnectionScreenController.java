package app.insa.clav.UIControllers;

import app.insa.clav.Core.DataBaseAccess;
import app.insa.clav.Core.Model;
import app.insa.clav.Core.Utilisateurs;
import com.jfoenix.controls.JFXButton;
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
    private TextField loginInputIn;

    @FXML
    private TextField pseudoInputIn;

    @FXML
    private JFXButton signInButton;

    @FXML
    private TextField loginInputUp;

    @FXML
    private TextField pseudoInputUp;

    @FXML
    private JFXButton signUpButton;

    @FXML
    private ProgressIndicator spinnerIn;

    @FXML
    private ProgressIndicator spinnerUp;

    @FXML
    private Label errorLabelIn;

    @FXML
    private Label errorLabelUp;

    @FXML
    private Label labelErrorInLogin;

    @FXML
    private Label labelErrorUpLogin;

    @FXML
    private Model model;

    private boolean isSubmittingIn;

    private boolean isSubmittingUp;
    private boolean isSubmittingNewPseudoIn;

    private DataBaseAccess dbAccess;

    private String loginUp;
    private String pseudoUp;
    private String pseudoIn;




    public ConnectionScreenController(){
        this.model = Model.getInstance();
        this.dbAccess = DataBaseAccess.getInstance();
        this.model.addPropertyChangeListener(this,"pseudoRefused");
        this.model.addPropertyChangeListener(this,"pseudoValide");
        isSubmittingIn = false;
        isSubmittingUp = false;
        isSubmittingNewPseudoIn = false;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    void submitConnection(ActionEvent event) {
        String login = this.loginInputIn.getText();
        if (!login.equals("") && !this.isSubmittingIn && !this.isSubmittingUp) {
            this.isSubmittingIn = true;
            this.spinnerIn.setVisible(true);
            if (this.dbAccess.LoginExist(login)) {
                String pseudo = this.dbAccess.getPseudoFromLogin(login);
                int id = this.dbAccess.getIdFromLogin(login);
                this.model.setUserId(id);
                this.model.choosePseudo(pseudo,true);
            } else {
                this.labelErrorInLogin.setVisible(true);
            }
        }
    }

    @FXML
    void submitSignUp(ActionEvent event) {
        this.loginUp = this.loginInputUp.getText();
        this.pseudoUp = this.pseudoInputUp.getText();
        System.out.println("login = " + loginUp + " and pseudo = " + pseudoUp);
        if (!this.loginUp.equals("") && !this.pseudoUp.equals("") && !this.isSubmittingIn && !this.isSubmittingUp){
            this.isSubmittingUp = true;
            this.spinnerUp.setVisible(true);
            boolean isLoginOk = this.dbAccess.isLoginUsed(this.loginUp);
            if (isLoginOk){
                this.model.choosePseudo(this.pseudoUp,false);
            }
            else{
                this.labelErrorUpLogin.setVisible(true);
                this.spinnerUp.setVisible(false);
                this.isSubmittingUp = false;
            }
        }
    }

    private void pseudoValideUp(){
        this.model.setUserId(this.dbAccess.addUtilisateur(this.loginUp,this.pseudoUp));
        this.model.sendPseudoValideBroadcast();
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()){
            case "pseudoValide" :
                this.model.deletePropertyChangeListener(this,"pseudoValide");
                this.model.deletePropertyChangeListener(this,"pseudoRefused");
                System.out.println("Pseudo valide");
                if (isSubmittingUp){
                    this.pseudoValideUp();
                }
                if (this.isSubmittingNewPseudoIn){
                    System.out.println("Update pseudo with " + this.model.user.getPseudo());
                    this.dbAccess.updatePseudo(this.model.user.getId(),this.pseudoIn);
                }
                this.isSubmittingUp = false;
                this.isSubmittingIn = false;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Stage mainStage = (Stage) signUpButton.getScene().getWindow();
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
                if (this.isSubmittingIn) {
                    this.isSubmittingIn = false;
                    if (this.isSubmittingNewPseudoIn || this.pseudoInputIn.getText().equals("")) {
                        this.isSubmittingNewPseudoIn = false;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                errorLabelIn.setVisible(true);
                                spinnerIn.setVisible(false);
                            }
                        });
                    }
                    else{
                        this.pseudoIn = this.pseudoInputIn.getText();
                        this.isSubmittingNewPseudoIn = true;
                        System.out.println("Pseudo refused, trying new " + this.pseudoIn);
                        this.model.choosePseudo(this.pseudoIn,true);
                    }
                }else if (this.isSubmittingUp){
                    this.isSubmittingUp = false;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            errorLabelUp.setVisible(true);
                            spinnerUp.setVisible(false);
                        }
                    });
                }
        }
    }
}
