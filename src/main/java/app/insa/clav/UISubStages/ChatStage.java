package app.insa.clav.UISubStages;

import app.insa.clav.Core.Model;
import app.insa.clav.Reseau.TCPChatConnection;
import app.insa.clav.UIControllers.ChatWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatStage extends Stage {

    public ChatStage(TCPChatConnection tcpCo){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/chatWindow.fxml"));
        try {
            Parent rootChat = fxmlLoader.load();
            ChatWindowController ctrl = fxmlLoader.getController();
            ctrl.setTCPCo(tcpCo);
            this.setTitle("Chat Room");
            this.setScene(new Scene(rootChat, 500, 400));
            this.setMinHeight(400);
            this.setMinWidth(500);
            this.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
