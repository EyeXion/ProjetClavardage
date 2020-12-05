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
            ResultSet rs = prSt.executeQuery();
            if (rs.first()){
                pseudo = rs.getString("pseudo");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return pseudo;
    }

    public boolean isLoginUsed(String login){
        String loginAux = null;
        String preparedQuery = "SELECT * FROM Utilisateurs WHERE login=?";
        PreparedStatement prSt = null;
        try {
            prSt = con.prepareStatement(preparedQuery);
            prSt.setString(1, login);
            ResultSet rs = prSt.executeQuery();
            if (rs.first()){
                loginAux = rs.getString("login");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("login Aux = " + loginAux);
        return loginAux == null;
    }

    public boolean LoginExist(String login) {
        String Query = "SELECT * FROM Utilisateurs WHERE login = '" + login + "'";
        System.out.println(Query);
        boolean Ok = false;
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(Query);
            Ok = rs.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Ok;
    }

    public int getIdFromLogin(String login) {
        String Query = "SELECT id FROM Utilisateurs WHERE login = '" + login + "'";
        int id = -1;
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(Query);
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;
    }

    public int addUtilisateur(String login, String pseudo){
        String loginAux = null;
        String preparedQuery = "INSERT INTO Utilisateurs (`login`, `pseudo`,`password`) VALUES (?,?,'pouet')";
        PreparedStatement prSt = null;
        try {
            prSt = con.prepareStatement(preparedQuery);
            prSt.setString(1, login);
            prSt.setString(2,pseudo);
            prSt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        preparedQuery = "SELECT * FROM Utilisateurs WHERE login=?";
        int id = 0;
        try {
            prSt = con.prepareStatement(preparedQuery);
            prSt.setString(1, login);
            ResultSet rs = prSt.executeQuery();
            if (rs.first()){
                id = rs.getInt("id");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;
    }

}
