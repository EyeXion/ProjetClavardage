package app.insa.clav.UIControllers;

import app.insa.clav.Core.DataBaseAccess;
import app.insa.clav.Core.Model;
import app.insa.clav.Core.Utilisateurs;
import app.insa.clav.Messages.MessageChatTxt;
import app.insa.clav.Messages.MessageDisplay;
import app.insa.clav.Messages.MessageDisplayFile;
import app.insa.clav.Reseau.TCPChatConnection;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

    @FXML
    private JFXButton pickFileButton;

    @FXML
    private JFXButton removeFileButton;

    @FXML
    private Label labelFile;

    private ObservableList<MessageDisplay> listMessages;

    private DataBaseAccess dbAccess;

    private int localUserId;

    private Image imageSource;

    private Image imageRemote;

    private File filePicked;

    private FileChooser fileChooser;

    public ChatWindowController(){
        this.model = Model.getInstance();
        model.addPropertyChangeListener(this,"newUserConnected");
        this.filePicked = null;
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
                    setPadding(new Insets(0,0,0,0));
                }else{
                    if (item.getType() == 1) {
                        if (item.getSourceId() == remoteUser.getId()) {
                            setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.AZURE.toString()), null, null)));
                            ImageView img = new ImageView();
                            img.setImage(imageRemote);
                            setGraphic(img);
                            setContentDisplay(ContentDisplay.RIGHT);
                            setAlignment(Pos.CENTER_RIGHT);
                            setTextAlignment(TextAlignment.RIGHT);
                            setPadding(new Insets(10, 0, 10, param.getWidth() * 0.3));
                        } else {
                            setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.CORNSILK.toString()), null, null)));
                            ImageView img = new ImageView();
                            img.setImage(imageSource);
                            setGraphic(img);
                            setContentDisplay(ContentDisplay.LEFT);
                            setAlignment(Pos.CENTER_LEFT);
                            setTextAlignment(TextAlignment.LEFT);
                            setPadding(new Insets(10, param.getWidth() * 0.3, 10, 0));
                        }
                        setGraphicTextGap(5.0);
                        setBorder(new Border(new BorderStroke(Paint.valueOf(Color.LIGHTGRAY.toString()), BorderStrokeStyle.DASHED, new CornerRadii(40.0), BorderStroke.THIN)));
                        setMaxWidth(param.getPrefWidth() * 0.9);
                        setPrefWidth(param.getPrefWidth() * 0.9);
                        // allow wrapping
                        setWrapText(true);
                        setText(item.getPayload());
                    }
                    else if (item.getType() == 2){
                        setText(null);
                        Hyperlink hyperlink = new Hyperlink();
                        MessageDisplayFile msgFile = (MessageDisplayFile) item;
                        hyperlink.setText(msgFile.getPayload());
                        hyperlink.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                if (msgFile.getDBId() == -1){
                                    fileChooser = new FileChooser();
                                    fileChooser.setInitialFileName(msgFile.getPayload());
                                    File savedFile = fileChooser.showSaveDialog(rootAnchor.getScene().getWindow());
                                    Path original = Path.of(msgFile.getFile().toURI());
                                    Path dest = Path.of(savedFile.toURI());
                                    try {
                                        Files.copy(original,dest, StandardCopyOption.REPLACE_EXISTING);
                                    } catch (IOException ioException) {
                                        ioException.printStackTrace();
                                    }
                                }
                                else {
                                    fileChooser = new FileChooser();
                                    fileChooser.setInitialFileName(msgFile.getPayload());
                                    File savedFile = fileChooser.showSaveDialog(rootAnchor.getScene().getWindow());
                                    dbAccess.getFile(msgFile.getDBId(), savedFile, localUserId, remoteUser.getId());
                                }
                            }
                        });
                        HBox hbox = new HBox();
                        hbox.setSpacing(2.0);
                        if (item.getSourceId() == remoteUser.getId()) {
                            hbox.setAlignment(Pos.CENTER_RIGHT);
                            hbox.setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.TRANSPARENT.toString()), null, null)));
                            setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.AZURE.toString()), null, null)));
                            ImageView img = new ImageView();
                            img.setImage(imageRemote);
                            hbox.getChildren().addAll(hyperlink, img);
                            setGraphic(hbox);
                            setContentDisplay(ContentDisplay.RIGHT);
                            setAlignment(Pos.CENTER_RIGHT);
                            setTextAlignment(TextAlignment.RIGHT);
                            setPadding(new Insets(10, 0, 10, param.getWidth() * 0.3));
                        } else {
                            hbox.setAlignment(Pos.CENTER_LEFT);
                            hbox.setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.TRANSPARENT.toString()), null, null)));
                            setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.CORNSILK.toString()), null, null)));
                            ImageView img = new ImageView();
                            img.setImage(imageSource);
                            hbox.getChildren().addAll(img, hyperlink);
                            setGraphic(hbox);
                            setContentDisplay(ContentDisplay.LEFT);
                            setAlignment(Pos.CENTER_LEFT);
                            setTextAlignment(TextAlignment.LEFT);
                            setPadding(new Insets(10, param.getWidth() * 0.3, 10, 0));
                        }
                    } else if (item.getType() == 3){
                        setText(null);
                        MessageDisplayFile msgFile = (MessageDisplayFile) item;
                        Hyperlink hyperlink = new Hyperlink();
                        hyperlink.setText(msgFile.getPayload());
                        hyperlink.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                fileChooser = new FileChooser();
                                fileChooser.setInitialFileName(msgFile.getPayload());
                                File savedFile = fileChooser.showSaveDialog(rootAnchor.getScene().getWindow());
                                Path original = Path.of(msgFile.getFile().toURI());
                                Path dest = Path.of(savedFile.toURI());
                                try {
                                    Files.copy(original,dest, StandardCopyOption.REPLACE_EXISTING);
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }
                            }
                        });
                        Image imageFileSource = null;
                        try {
                            imageFileSource = new Image(msgFile.getFile().toURI().toURL().toExternalForm());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        ImageView imageFileView = new ImageView();
                        imageFileView.setImage(imageFileSource);
                        HBox hbox = new HBox();
                        hbox.setSpacing(2.0);
                        hbox.setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.TRANSPARENT.toString()), null, null)));
                        VBox vbox = new VBox();
                        vbox.setSpacing(5.0);
                        vbox.setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.TRANSPARENT.toString()), null, null)));
                        imageFileView.setPreserveRatio(true);
                        imageFileView.fitWidthProperty().bind(messageList.widthProperty().multiply(0.5));
                        imageFileView.fitHeightProperty().bind(hbox.heightProperty());
                        vbox.setAlignment(Pos.TOP_CENTER);
                        vbox.getChildren().addAll(imageFileView,hyperlink);
                        vbox.setPrefWidth(messageList.getWidth() * 0.5);
                        if (item.getSourceId() == remoteUser.getId()) {
                            hbox.setAlignment(Pos.CENTER_RIGHT);setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.AZURE.toString()), null, null)));
                            ImageView img = new ImageView();
                            img.setImage(imageRemote);
                            hbox.getChildren().addAll(vbox, img);
                            setGraphic(hbox);
                            setContentDisplay(ContentDisplay.RIGHT);
                            setAlignment(Pos.CENTER_RIGHT);
                            setTextAlignment(TextAlignment.RIGHT);
                            setPadding(new Insets(10, 0, 10, param.getWidth() * 0.3));
                        } else {
                            hbox.setAlignment(Pos.CENTER_LEFT);
                            setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.CORNSILK.toString()), null, null)));
                            ImageView img = new ImageView();
                            img.setImage(imageSource);
                            hbox.getChildren().addAll(img, vbox);
                            setGraphic(hbox);
                            setContentDisplay(ContentDisplay.LEFT);
                            setAlignment(Pos.CENTER_LEFT);
                            setTextAlignment(TextAlignment.LEFT);
                            setPadding(new Insets(10, param.getWidth() * 0.3, 10, 0));
                        }
                    }
                }
            }
        });
    }

    /**
     * Returns the message history and puts it in the ListView
     */
    private void getHistory(){
        this.dbAccess = DataBaseAccess.getInstance();
        if (remoteUser == null) {
            System.out.println("\n\n\nATTTENNNNTIIONNN\n \n\n");
        }
        if (!this.dbAccess.isTableCreated(remoteUser.getId(), localUserId)) {
            System.out.println("ATTENTION TU N'AS PAS CREE LA TABLE");
        }
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
        if (this.tcpCo.isOutdoor()) {
            this.pickFileButton.setVisible(false);
            this.pickFileButton.setDisable(true);
        }
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
                MessageDisplay msgDisp = new MessageDisplay(remoteUser.getId(), msg.date, msg.payload,1);
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
                break;
            case "fileReceived" :
                MessageDisplayFile msgFile = tcpCo.getMessageFileReceived();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        listMessages.add(msgFile);
                        int size = messageList.getItems().size();
                        messageList.scrollTo(size - 1);
                    }
                });
        }
    }

    /** Handler to send message
     * @param actionEvent
     */
    public void buttonSendMessageClicked(ActionEvent actionEvent) {
            this.sendButton.setDisable(true);
            String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
            String payload = this.messageInput.getText();
            if (!payload.equals("")) {
                MessageDisplay msg = new MessageDisplay(model.user.getId(), timeStamp, payload, 1);
                this.listMessages.add(msg);
                this.messageInput.clear();
                this.tcpCo.sendMessageTxt(msg);
                this.dbAccess.addMessage(this.localUserId, this.remoteUser.getId(), msg);
            }

            //We send the File if the filed is not null
            if (this.filePicked != null) {
                String ext = FilenameUtils.getExtension(this.filePicked.getPath());
                int type = 2;
                switch (ext) {
                    case "png":
                    case "gif":
                    case "jpeg":
                    case "svg":
                    case "jpg":
                        type = 3;
                        break;
                }
                MessageDisplayFile msgFile = new MessageDisplayFile(model.user.getId(), timeStamp, this.filePicked.getName(), type, this.filePicked, ext, -1);
                this.listMessages.add(msgFile);
                this.tcpCo.sendMessageFile(msgFile);
                this.dbAccess.addMessage(this.localUserId, this.remoteUser.getId(), msgFile);
                this.filePicked = null;
                this.labelFile.setVisible(false);
            }
            this.sendButton.setDisable(false);
            int size = messageList.getItems().size();
            messageList.scrollTo(size - 1);
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
        mainStage.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                System.out.println("Fire");
                this.sendButton.fire();
            }
        });
    }

    @FXML
    void showDate() {
        String dateMsg = messageList.getFocusModel().getFocusedItem().getDate();
        this.dateMsg.setText(dateMsg);
    }

    @FXML
    void pickFile(ActionEvent event) {
        File previousFile = this.filePicked;
        this.fileChooser = new FileChooser();
        this.filePicked = this.fileChooser.showOpenDialog(this.rootAnchor.getScene().getWindow());
        if (this.filePicked != null){
            if (filePicked.length() / (1024*1024) > 5){
                this.labelFile.setText("File too large, limit is 5Mb");
                this.labelFile.setVisible(true);
                this.filePicked = null;
            }
            else {
                this.labelFile.setText("File : " + this.filePicked.getName());
                this.labelFile.setVisible(true);
            }
        }
        else{
            this.filePicked = previousFile;
        }
        this.sendButton.requestFocus();
    }

    @FXML
    void removeFile(ActionEvent event) {
        this.labelFile.setVisible(false);
        this.filePicked = null;
        this.sendButton.requestFocus();
    }
}
