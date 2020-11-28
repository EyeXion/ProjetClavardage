package app.insa.clav.Core;//main pour les tests

import app.insa.clav.UIControllers.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Test extends Application{


    @Override
    public void start(Stage primaryStage) throws Exception{
        String[] args = this.getParameters().getUnnamed().toArray(new String[0]);
        int id = Integer.parseInt(args[0]);
        int inputPort = Integer.parseInt(args[1]);
        int outPutPort = Integer.parseInt(args[2]);

        Model model = new Model(id,inputPort, outPutPort);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainWindow.fxml"));
        Parent root =fxmlLoader.load();
        MainWindowController mainCtrl = fxmlLoader.getController();
        mainCtrl.setupController(model);
        primaryStage.setTitle("Messenger, en mieux");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(600);
        primaryStage.show();
        model.openInputUDP();
    }

    public static void main(String[] arg){
        //String ipAdress = arg[0];
        launch(arg);

        /* while (true){
            Scanner myScan = new Scanner(System.in);
            System.out.println("Entrez un pseudo svp : ");
            String pseudo = myScan.nextLine();
            //model.ctrl.choosePseudo(pseudo);
        } */
    }
}
