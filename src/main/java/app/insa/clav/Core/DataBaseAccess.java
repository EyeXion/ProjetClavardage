package app.insa.clav.Core;

import java.sql.*;

public class DataBaseAccess {

    private static DataBaseAccess instance = null;

    public Connection con;

    private DataBaseAccess() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testDBChat?useSSL=false","root","0000");
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

    public String getPseudoFromLogin(String login) {
        String pseudo = null;
        String preparedQuery = "SELECT * FROM Utilisateurs WHERE login=?";
        PreparedStatement prSt = null;
        try {
            prSt = con.prepareStatement(preparedQuery);
            prSt.setString(1, login);
            System.out.println(prSt.toString());
            ResultSet rs = prSt.executeQuery();
            if (rs.first()){
                pseudo = rs.getString("pseudo");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return pseudo;
    }

}
