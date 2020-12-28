package app.insa.clav.UIControllers;

import app.insa.clav.Core.DataBaseAccess;
import app.insa.clav.Core.Model;
import app.insa.clav.Core.Utilisateurs;
import app.insa.clav.Messages.MessageChatTxt;
import app.insa.clav.Messages.MessageDisplay;
import app.insa.clav.Reseau.TCPChatConnection;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class ChatWindowController implements Initializable, PropertyChangeListener {

    @FXML
    private AnchorPane rootAnchor;

    private Model model;

    private TCPChatConnection tcpCo;

    private Utilisateurs remoteUser;

    @FXML
    private ListView<MessageDisplay> messageList;


    @FXML
    private ContextMenu contextMenu;

    @FXML
    private MenuItem dateMsg;


    @FXML
    private TextField messageInput;

    @FXML
    private JFXButton sendButton;

    private ObservableList<MessageDisplay> listMessages;

    private DataBaseAccess dbAccess;

    private int localUserId;

    private Image imageSource;

    private Image imageRemote;

    public ChatWindowController(){
        this.model = Model.getInstance();
        model.addPropertyChangeListener(this,"newUserConnected");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.localUserId = model.user.getId();
        this.imageSource = new Image(getClass().getResourceAsStream("/logos/pinkDot.png"));
        this.imageRemote = new Image(getClass().getResourceAsStream("/logos/blueDot.png"));
        messageList.setCellFactory(param -> new ListCell<MessageDisplay>(){
            @Override
            protected void updateItem(MessageDisplay item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item==null) {
                    setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.TRANSPARENT.toString()),null,null)));
                    setGraphic(null);
                    setText(null);
                    // other stuff to do...
                }else{
                    if (item.getSourceId() == remoteUser.getId()){
                        setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.AZURE.toString()),null,null)));
                        ImageView img = new ImageView();
                        img.setImage(imageRemote);
                        setGraphic(img);
                        setContentDisplay(ContentDisplay.RIGHT);
                        setAlignment(Pos.CENTER_RIGHT);
                        setTextAlignment(TextAlignment.RIGHT);
                        setPadding(new Insets(10,0,10,param.getWidth()*0.3));
                    }
                    else{
                        setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.CORNSILK.toString()),null,null)));
                        ImageView img = new ImageView();
                        img.setImage(imageSource);
                        setGraphic(img);
                        setContentDisplay(ContentDisplay.LEFT);
                        setAlignment(Pos.CENTER_LEFT);
                        setTextAlignment(TextAlignment.LEFT);
                        setPadding(new Insets(10,param.getWidth()*0.3,10,0));
                    }
                    setGraphicTextGap(5.0);
                    setBorder(new Border(new BorderStroke(Paint.valueOf(Color.LIGHTGRAY.toString()),BorderStrokeStyle.DASHED,new CornerRadii(40.0),BorderStroke.THIN)));
                    setMaxWidth(param.getPrefWidth()*0.9);
                    setPrefWidth(param.getPrefWidth()*0.9);
                    // allow wrapping
                    setWrapText(true);
                    setText(item.getPayload());
                }
            }
        });
    }

    /**
     * Returns the message history and puts it in the ListView
     */
    private void getHistory(){
        this.dbAccess = DataBaseAccess.getInstance();
        ArrayList<MessageDisplay> history = this.dbAccess.getMessageHistory(this.localUserId,remoteUser.getId());
        this.listMessages = FXCollections.observableList(history);
        this.messageList.setItems(this.listMessages);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int size = messageList.getItems().size();
                messageList.scrollTo(size - 1);
                Stage chatStage = (Stage) rootAnchor.getScene().getWindow();
                chatStage.setTitle("Chat room with " + remoteUser.getPseudo());
            }
        });
    }

    /** Sets the TcpCo attribute and adds propertyChangeListener for the TCPChatConnection
     * @param tcpCo
     */
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
                MessageDisplay msgDisp = new MessageDisplay(remoteUser.getId(), msg.date, msg.payload);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        listMessages.add(msgDisp);
                        int size = messageList.getItems().size();
                        messageList.scrollTo(size - 1);
                    }
                });
                break;
            case "connectionChatClosed":
            case "userDisconnected" :
                model.notifyCloseChat(tcpCo);
                Stage mainStage = (Stage) rootAnchor.getScene().getWindow();
                Platform.runLater(mainStage::close);
                break;
            case "newUserConnected" :
                if ((int) evt.getNewValue() == remoteUser.getId()) {
                    this.remoteUser = model.getUserFromId(remoteUser.getId());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Stage chatStage = (Stage) rootAnchor.getScene().getWindow();
                            chatStage.setTitle("Chat room with " + remoteUser.getPseudo());
                        }
                    });
                }
        }
    }

    /** Handler to send message
     * @param actionEvent
     */
    public void buttonSendMessageClicked(ActionEvent actionEvent) {
        String payload = this.messageInput.getText();
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        MessageDisplay msg = new MessageDisplay(model.user.getId(),timeStamp,payload);
        this.listMessages.add(msg);
        int size = messageList.getItems().size();
        messageList.scrollTo(size - 1);
        this.messageInput.clear();
        this.tcpCo.sendMessageTxt(msg);
        this.dbAccess.addMessage(this.localUserId,this.remoteUser.getId(),msg);
    }

    /**
     * Sets handler chen Window closed
     */
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

    @FXML
    void showDate() {
        String dateMsg = messageList.getFocusModel().getFocusedItem().getDate();
        this.dateMsg.setText(dateMsg);
    }
}
