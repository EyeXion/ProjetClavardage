package app.insa.clav.UIControllers;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * splash screen qui s'affiche quand on ouvre l'app
 */
public class SplashScreenController implements Initializable {

    /**
     * object qui "wrap" le tout
     */
    @FXML
    private StackPane rootSplash;

    /**
     * c1 c2 c3 les petites cercles qui tournent en haut de la page
     */
    @FXML
    private Circle c1;

    @FXML
    private Circle c2;

    @FXML
    private Circle c3;


    public SplashScreenController(){}


    /**
     * Appelé par le loader FXML
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.setRotateCircles(c1,true,360,10);
        this.setRotateCircles(c2,true,180,18);
        this.setRotateCircles(c3,true,345,24);


        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2),rootSplash);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2),rootSplash);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);

        fadeIn.play();

        fadeIn.setOnFinished((e) -> {
            fadeOut.play();
        });

        fadeOut.setOnFinished((e) -> {
            Stage mainStage = (Stage) rootSplash.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainWindow.fxml"));
            try {
                Parent rootMainScreen = fxmlLoader.load();
                Scene scene = new Scene(rootMainScreen);
                mainStage.setScene(scene);
                mainStage.show();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

    }

    /**
     * permet de faire tourner un cercle
     * @param c
     * @param reverse
     * @param angle
     * @param time
     */
    private void setRotateCircles(Circle c, boolean reverse, int angle, int time){
        RotateTransition rot = new RotateTransition(Duration.seconds(time),c);

        rot.setByAngle(angle);
        rot.setAutoReverse(reverse);
        rot.setDelay(Duration.ZERO);
        rot.setRate(6);
        rot.setCycleCount(3);
        rot.play();
    }
}
