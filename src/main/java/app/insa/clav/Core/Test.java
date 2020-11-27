package app.insa.clav.Core;//main pour les tests

import java.util.Scanner;

public class Test {

    public static void main(String[] arg){
        //String ipAdress = arg[0];
        int id = Integer.parseInt(arg[0]);
        int inputPort = Integer.parseInt(arg[1]);
        int outPutPort = Integer.parseInt(arg[2]);

        Model model = new Model(id,inputPort, outPutPort);
        model.openInputUDP();
        Controller mainCtrl = new Controller(model);

        while (true){
            Scanner myScan = new Scanner(System.in);
            System.out.println("Entrez un pseudo svp : ");
            String pseudo = myScan.nextLine();
            //model.ctrl.choosePseudo(pseudo);
        }
    }
}
