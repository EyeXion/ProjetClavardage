package app.insa.clav;

import app.insa.clav.Core.Model;
import app.insa.clav.UIControllers.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{


    @Override
    public void start(Stage primaryStage) throws Exception{
        //String[] args = this.getParameters().getUnnamed().toArray(new String[0]);
        int inputPort = 5000;//Integer.parseInt(args[0]);
        int outPutPort = 6000; //Integer.parseInt(args[1]);
        int tcpListenerPort = 7000; //Integer.parseInt(args[2]);

        Model model = Model.getInstance(inputPort, outPutPort,tcpListenerPort);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/splashScreen.fxml"));
        Parent root =fxmlLoader.load();
        primaryStage.setTitle("Messenger, en mieux");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(600);
        primaryStage.setMaxHeight(450);
        primaryStage.setMaxWidth(650);
        primaryStage.show();
        model.openInputUDP();
        model.openTCPListener();
    }

    public static void main(String[] arg){
        //String ipAdress = arg[0];
        launch(arg);

        /*while (true){
            Scanner myScan = new Scanner(System.in);
            System.out.println("Entrez un pseudo svp : ");
            String pseudo = myScan.nextLine();
            //model.ctrl.choosePseudo(pseudo);
        } */
    }
}
