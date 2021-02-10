package app.insa.clav.UIControllers;

import app.insa.clav.Core.DataBaseAccess;
import app.insa.clav.Core.Model;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConnectionScreenController implements Initializable, PropertyChangeListener {


    /**
     * Text input login for sign in
     */
    @FXML
    private TextField loginInputIn;

    /**
     * Text input pseudo for sign in
     */
    @FXML
    private TextField pseudoInputIn;

    /**
     * Sign in button
     */
    @FXML
    private JFXButton signInButton;

    /**
     * Text input login for sign up
     */
    @FXML
    private TextField loginInputUp;

    /**
     * Text input pseudo for sign in
     */
    @FXML
    private TextField pseudoInputUp;

    /**
     * Sign up Button
     */
    @FXML
    private JFXButton signUpButton;

    /**
     * Spinner Sign in
     */
    @FXML
    private ProgressIndicator spinnerIn;

    /**
     * Spinner Sign Up
     */
    @FXML
    private ProgressIndicator spinnerUp;

    /**
     * Error text displayed when pseudo already used sign in
     */
    @FXML
    private Label errorLabelIn;

    /**
     * Error text displayed when pseudo already used sign up
     */
    @FXML
    private Label errorLabelUp;

    /**
     * Error text displayed when login wrong sign in
     */
    @FXML
    private Label labelErrorInLogin;

    /**
     * Error text displayed when login wrong sign up
     */
    @FXML
    private Label labelErrorUpLogin;

    @FXML
    private Model model;

    /**
     * True if trying to sign in
     */
    private boolean isSubmittingIn;

    /**
     * True if trying to sign up
     */
    private boolean isSubmittingUp;
    /**
     * True if pseudo in DB already used when sign in and trying new one
     */
    private boolean isSubmittingNewPseudoIn;


    /**
     * True if the user is outdoor
     */
    @FXML
    private JFXToggleButton isOutdoorUserButton;

    private DataBaseAccess dbAccess;

    private String loginUp;
    private String pseudoUp;
    private String pseudoIn;

    private boolean isOutdoor;




    public ConnectionScreenController(){
        this.dbAccess = DataBaseAccess.getInstance();
        System.out.println(this.dbAccess.toString());
        isSubmittingIn = false;
        isSubmittingUp = false;
        isSubmittingNewPseudoIn = false;
        this.isOutdoor = false;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**Called when button Sign In pushed
     * @param event
     */
    @FXML
    void submitConnection(ActionEvent event) {
        String login = this.loginInputIn.getText();
        //System.out.println(this.dbAccess == null);
        if (!login.equals("") && !this.isSubmittingIn && !this.isSubmittingUp) {
            this.isSubmittingIn = true;
            this.spinnerIn.setVisible(true);
            if (this.dbAccess.isLoginUsed(login)) {
                this.model = Model.getInstance();
                this.model.addPropertyChangeListener(this,"pseudoRefused");
                this.model.addPropertyChangeListener(this,"pseudoValide");
                String pseudo = this.dbAccess.getPseudoFromLogin(login);
                int id = this.dbAccess.getIdFromLogin(login);
                this.model.setUserId(id);
                if (this.isOutdoor) {
                    this.model.choosePseudoOutdoor(pseudo,true);
                } else {
                    model.configModelIndoor();
                    this.model.choosePseudo(pseudo,true);
                }
            } else {
                this.labelErrorInLogin.setVisible(true);
            }
        }
    }

    /**Called when Butto sign in pushed
     * @param event
     */
    @FXML
    void submitSignUp(ActionEvent event) {
        this.loginUp = this.loginInputUp.getText();
        this.pseudoUp = this.pseudoInputUp.getText();
        if (!this.loginUp.equals("") && !this.pseudoUp.equals("") && !this.isSubmittingIn && !this.isSubmittingUp){
            this.isSubmittingUp = true;
            this.spinnerUp.setVisible(true);
            boolean loginExist = this.dbAccess.isLoginUsed(this.loginUp);
            if (!loginExist){
                this.model = Model.getInstance();
                this.model.addPropertyChangeListener(this,"pseudoRefused");
                this.model.addPropertyChangeListener(this,"pseudoValide");
                if (this.isOutdoor) {
                    this.model.choosePseudoOutdoor(pseudoUp,true);
                } else {
                    model.configModelIndoor();
                    this.model.choosePseudo(pseudoUp,true);
                }
            }
            else{
                this.labelErrorUpLogin.setVisible(true);
                this.spinnerUp.setVisible(false);
                this.isSubmittingUp = false;
            }
        }
    }

    /**
     * Called when signing up and pseudo valid, used to update DB ad send pseudo Confirmation
     */
    private void pseudoValideUp(){
        System.out.println("Log et pseudo " + this.loginUp + this.pseudoUp);
        this.model.setUserId(this.dbAccess.addUtilisateur(this.loginUp,this.pseudoUp));
        this.model.sendPseudoValideBroadcast();
    }


    @FXML
    void outdoorUserAction(ActionEvent event) {
        //this.model.user.setOutdoor(this.isOutdoorUserButton.isSelected());
        this.isOutdoor = this.isOutdoorUserButton.isSelected();
    }

    /**
     * Handler of notifications sent by te model
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()){
            case "pseudoValide" :
                Stage mainStage = (Stage) loginInputIn.getScene().getWindow();
                mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent t) {
                        Model.getInstance().sendDeconnectionMessage();
                    }
                });
                this.model.deletePropertyChangeListener(this,"pseudoValide");
                this.model.deletePropertyChangeListener(this,"pseudoRefused");
                System.out.println("Pseudo valide");
                if (isSubmittingUp && !this.model.user.isOutdoor()){
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
