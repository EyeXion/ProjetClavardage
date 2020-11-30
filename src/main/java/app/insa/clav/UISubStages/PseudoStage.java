package app.insa.clav.UISubStages;

import app.insa.clav.Core.Model;
import app.insa.clav.UIControllers.PseudoWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe qui permet de créer la nouvelle fenetre pour le pseudo
 */
public class PseudoStage extends Stage {

    public PseudoStage(Model model){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/pseudoWindow.fxml"));
        try {
            Parent rootPseudo = fxmlLoader.load();
            this.setTitle("Messenger, en mieux");
            this.setScene(new Scene(rootPseudo, 340, 290));
            this.setMinHeight(290);
            this.setMinWidth(340);
            this.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
