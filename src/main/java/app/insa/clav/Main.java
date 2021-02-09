package app.insa.clav;

import app.insa.clav.Core.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{


    @Override
    public void start(Stage primaryStage) throws Exception{
        String[] args = this.getParameters().getUnnamed().toArray(new String[0]);
        String addrBroad = args[0];
        int udpListeningPort = Integer.parseInt(args[1]);
        String addrSrv = args[2];
        String addrBdd = args[3];
        String userBdd = args[4];
        String mdpBdd = args[5];
        String nomBDD = args[6];

        Model model = Model.getInstance(addrBroad, udpListeningPort, this, addrBdd, userBdd, mdpBdd, nomBDD);

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

    public static void main(String[] args){
        //String ipAdress = arg[0];
        launch(args);

        /*while (true){
            Scanner myScan = new Scanner(System.in);
            System.out.println("Entrez un pseudo svp : ");
            String pseudo = myScan.nextLine();
            //model.ctrl.choosePseudo(pseudo);
        } */
    }
}
