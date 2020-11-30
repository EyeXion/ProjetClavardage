package app.insa.clav.UIControllers;

import app.insa.clav.Core.Model;
import app.insa.clav.Core.Utilisateurs;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controleur du modèle MVC pour la mainWindow. Interface entre le modèle (Application) et l'UI.
 * Un controleur pour chaque fenêtre (main window ou fenetre de clavardage).
 * Il est instancié par le vue direcement.
 */
public class MainWindowController implements PropertyChangeListener, Initializable {

    /**
     * Application. UNE SEULE INSTANCE DANS TOUT LE PROGRAMME (faire un singleton ?)
     */
    private Model model;

    /**
     * Boutton piur ouvirir le Drawerr
     */
    @FXML
    private JFXHamburger mainHamburger;

    /**
     * mainDrawer. Le contenu est controllé par le MainDrawerController
     */
    @FXML
    private JFXDrawer mainDrawer;

    /**
     * Affiche les utilisateurs connectés
     */
    @FXML
    private JFXListView<String> userListView;

    /**
     * Titre en haut de la page
     */
    @FXML
    private Label mainLabel;


    /**
     * AnchorPane qui "wrap" toute la scene
     */
    @FXML
    private AnchorPane rootPane;


    /**
     * transition quand on clique sur le hamburger
     */
    private HamburgerSlideCloseTransition hamburgerClick1;

    /**
     * Liste observable sur la ListView va suivre
     */
    private ObservableList<String> listUsers;

    /**
     * Contructeur. Il crée lui même l'UI (plus tard on mettra quel type de fenetre en argument)
     */
    public MainWindowController(){
        this.model = Model.getInstance();
        this.model.addPropertyChangeListener(this,"pseudoValide");
        this.model.addPropertyChangeListener(this,"newUserConnected");
    }

    /**
     * Appele lors de la creation de la classe par le loader FXML
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainDrawerContent.fxml"));
            Parent drawerBox = fxmlLoader.load();
            this.mainDrawer.setSidePane(drawerBox);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.listUsers = FXCollections.observableArrayList(this.model.getUserList().stream().map(Utilisateurs::getPseudo).collect(Collectors.toList()));
        this.userListView.setItems(this.listUsers);
        this.hamburgerClick1 = new HamburgerSlideCloseTransition(this.mainHamburger);
        this.hamburgerClick1.setRate(-1);
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
     * handler signaux emis par la model
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()){
            case "pseudoValide" :
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        mainLabel.setText("Welcome, " + model.user.getPseudo() + "!");
                    }
                });
                break;
            case "newUserConnected" :
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        listUsers = FXCollections.observableArrayList(model.getUserList().stream().map(Utilisateurs::getPseudo).collect(Collectors.toList()));
                        userListView.setItems(listUsers);
                    }
                });
        }
    }

    /**
     * handler click sur la hamburger. Ferme ou ouvre le mainDrawer
     * @param evt
     */
    public void clickMainHamburgerHandler(MouseEvent evt){
        this.hamburgerClick1.setRate(this.hamburgerClick1.getRate()*-1);
        this.hamburgerClick1.play();

        if (this.mainDrawer.isOpened()){
            this.mainDrawer.close();
        }
        else{
            this.mainDrawer.open();
        }
    }

}
