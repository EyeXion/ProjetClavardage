package app.insa.clav;

import app.insa.clav.Core.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main extends Application{


    @Override
    public void start(Stage primaryStage) throws Exception{
        String addrBroad;
        int udpListeningPort;
        String urlServeur;
        String addrBdd;
        String userBdd;
        String mdpBdd;
        String nomBdd;

        try {
            //to load application's properties, we use this class
            Properties mainProperties = new Properties();

            FileInputStream file;

            //the base folder is ./, the root of the main.properties file
            String path = "./config.properties";

            //load the file handle for main.properties
            file = new FileInputStream(path);

            //load all the properties from this file
            mainProperties.load(file);

            //we have loaded the properties, so close the file handle
            file.close();


            System.out.println(mainProperties.getProperty("app.localNetIPAddress"));
            System.out.println(mainProperties.getProperty("app.udpPortNumber"));
            System.out.println(mainProperties.getProperty("app.servletURL"));
            System.out.println(mainProperties.getProperty("app.dataBaseAddress"));
            System.out.println(mainProperties.getProperty("app.dataBaseUser"));
            System.out.println(mainProperties.getProperty("app.dataBasePasswd"));
            System.out.println(mainProperties.getProperty("app.dataBaseName"));

            addrBroad = mainProperties.getProperty("app.localNetIPAddress");
            udpListeningPort = Integer.parseInt(mainProperties.getProperty("app.udpPortNumber"));
            urlServeur = mainProperties.getProperty("app.servletURL");
            addrBdd = mainProperties.getProperty("app.dataBaseAddress");
            userBdd = mainProperties.getProperty("app.dataBaseUser");
            mdpBdd = mainProperties.getProperty("app.dataBasePasswd");
            nomBdd = mainProperties.getProperty("app.dataBaseName");
            Model model = Model.getInstance(addrBroad, udpListeningPort, this, addrBdd, userBdd, mdpBdd, nomBdd, urlServeur);
        } catch (IOException e) {
            e.printStackTrace();
        }


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/splashScreen.fxml"));
        Parent root =fxmlLoader.load();
        primaryStage.setTitle("Messenger, en mieux");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(600);
        primaryStage.setMaxHeight(450);
        primaryStage.setMaxWidth(650);
        primaryStage.show();
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
