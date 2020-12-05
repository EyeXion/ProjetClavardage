package app.insa.clav.UIControllers;

import app.insa.clav.Core.Model;
import app.insa.clav.Core.Utilisateurs;
import app.insa.clav.Messages.MessageChatTxt;
import app.insa.clav.Reseau.TCPChatConnection;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ChatWindowController implements Initializable, PropertyChangeListener {

    @FXML
    private AnchorPane rootAnchor;

    private Model model;

    private TCPChatConnection tcpCo;

    private Utilisateurs remoteUser;

    @FXML
    private ListView<String> messageList;

    @FXML
    private TextField messageInput;

    @FXML
    private JFXButton sendButton;

    private ObservableList<String> listMessages;


    public ChatWindowController(){
        this.model = Model.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.listMessages = FXCollections.observableList(new ArrayList<String>());
        this.messageList.setItems(this.listMessages);
    }

    public void setTCPCo(TCPChatConnection tcpCo){
        this.tcpCo = tcpCo;
        tcpCo.addPropertyChangeListener(this);
        int remoteUserId = tcpCo.remoteUserId;
        this.remoteUser = model.getUserFromId(remoteUserId);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()){
            case "messageTextReceivedTCP" :
                MessageChatTxt msg = (MessageChatTxt) tcpCo.getMessageReceived();
                String payload = msg.payload;
                Platform.runLater(() -> this.listMessages.add(payload));
        }
    }

    public void buttonSendMessageClicked(ActionEvent actionEvent) {
        String payload = model.user.getPseudo() + " : " + this.messageInput.getText();
        this.listMessages.add(payload);
        this.messageInput.clear();
        this.tcpCo.sendMessageTxt(payload);
    }
}
