package app.insa.clav.UIControllers;

import app.insa.clav.Core.DataBaseAccess;
import app.insa.clav.Core.Model;
import app.insa.clav.Core.Utilisateurs;
import app.insa.clav.Messages.MessageChatTxt;
import app.insa.clav.Messages.MessageHistoryList;
import app.insa.clav.Reseau.TCPChatConnection;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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

    private DataBaseAccess dbAccess;

    private int localUserId;


    public ChatWindowController(){
        this.model = Model.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.localUserId = model.user.getId();
    }

    private void getHistory(){
        this.dbAccess = DataBaseAccess.getInstance();
        ArrayList<MessageHistoryList> history = this.dbAccess.getMessageHistory(this.localUserId,remoteUser.getId());
        ArrayList<String> listMessagesAux = new ArrayList<String>();
        for (MessageHistoryList msg : history){
            listMessagesAux.add(msg.getPayload() + "****Envoyé à " + msg.getDate());
        }
        this.listMessages = FXCollections.observableList(listMessagesAux);
        this.messageList.setItems(this.listMessages);
    }

    public void setTCPCo(TCPChatConnection tcpCo){
        this.tcpCo = tcpCo;
        tcpCo.addPropertyChangeListener(this);
        int remoteUserId = tcpCo.remoteUserId;
        this.remoteUser = model.getUserFromId(remoteUserId);
        this.getHistory();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()){
            case "messageTextReceivedTCP" :
                MessageChatTxt msg = (MessageChatTxt) tcpCo.getMessageReceived();
                String payload = msg.payload;
                Platform.runLater(() -> this.listMessages.add(payload));
                break;
            case "connectionChatClosed":
                tcpCo.sendCloseChat();
                model.notifyCloseChat(tcpCo);
                Stage mainStage = (Stage) rootAnchor.getScene().getWindow();
                Platform.runLater(mainStage::close);
                break;
        }
    }

    public void buttonSendMessageClicked(ActionEvent actionEvent) {
        String payload = model.user.getPseudo() + " : " + this.messageInput.getText();
        this.listMessages.add(payload);
        this.messageInput.clear();
        this.tcpCo.sendMessageTxt(payload);
        this.dbAccess.addMessage(this.localUserId,this.remoteUser.getId(),payload);
    }

    public void setHandler() {
        Stage mainStage = (Stage) rootAnchor.getScene().getWindow();
        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                tcpCo.sendCloseChat();
                model.notifyCloseChat(tcpCo);
                Platform.runLater(mainStage::close);
            }
        });
    }
}
