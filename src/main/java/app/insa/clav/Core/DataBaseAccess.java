package app.insa.clav.Core;

import java.sql.*;

public class DataBaseAccess {

    private static DataBaseAccess instance = null;

    public Connection con;

    private DataBaseAccess() {
        try {
            con = DriverManager.getConnection("jdbc:localhost:testDBChat");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static DataBaseAccess getInstance() {
        synchronized (DataBaseAccess.class) {
            DataBaseAccess res = instance;
            if (res == null) {
                res = new DataBaseAccess();
            }
            return res;
        }
    }

    public String getPseudoFromLogin(String login){
        //Faire la requete à la BDD
        return "";
    }

}
