package app.insa.clav.Core;

import app.insa.clav.Messages.Message;
import app.insa.clav.Messages.MessageHistoryList;

import java.sql.*;
import java.util.ArrayList;

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
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testDBChat?useSSL=false", "root", "0000");
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
            if (rs.first()) {
                pseudo = rs.getString("pseudo");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return pseudo;
    }

    public boolean isLoginUsed(String login) {
        String loginAux = null;
        String preparedQuery = "SELECT * FROM Utilisateurs WHERE login=?";
        PreparedStatement prSt = null;
        try {
            prSt = con.prepareStatement(preparedQuery);
            prSt.setString(1, login);
            ResultSet rs = prSt.executeQuery();
            if (rs.first()) {
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

    public int addUtilisateur(String login, String pseudo) {
        String loginAux = null;
        String preparedQuery = "INSERT INTO Utilisateurs (`login`, `pseudo`,`password`) VALUES (?,?,'pouet')";
        PreparedStatement prSt = null;
        try {
            prSt = con.prepareStatement(preparedQuery);
            prSt.setString(1, login);
            prSt.setString(2, pseudo);
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
            if (rs.first()) {
                id = rs.getInt("id");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;
    }

    public ArrayList<MessageHistoryList> getMessageHistory(int id1, int id2) {
        ArrayList<MessageHistoryList> history = new ArrayList<MessageHistoryList>();
        int idPetit;
        int idGrand;
        if (id1 > id2) {
            idGrand = id1;
            idPetit = id2;
        } else {
            idGrand = id2;
            idPetit = id1;
        }
        String nomTable = "Chat" + idPetit + "_" + idGrand;
        String preparedQuery = "SELECT * FROM " + nomTable;
        PreparedStatement prSt = null;
        try {
            prSt = con.prepareStatement(preparedQuery);
            ResultSet rs = prSt.executeQuery();
            while (rs.next()){
                MessageHistoryList msg = new MessageHistoryList();
                msg.setSourceId(rs.getInt(2));
                msg.setDate(rs.getTimestamp(3).toString());
                msg.setPayload(rs.getString(4));
                history.add(msg);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return history;
    }

    public void addMessage(int idLocal, int idRemote, String payload){
        int idPetit;
        int idGrand;
        if (idLocal > idRemote) {
            idGrand = idLocal;
            idPetit = idRemote;
        } else {
            idGrand = idRemote;
            idPetit = idLocal;
        }
        String nomTable = "Chat" + idPetit + "_" + idGrand;
        String preparedQuery = "INSERT INTO `" + nomTable + "`(`sourceId`, `date`, `payload`) VALUES (?,CURRENT_TIME,?)";
        PreparedStatement prSt = null;
        try {
            prSt = con.prepareStatement(preparedQuery);
            prSt.setInt(1,idLocal);
            prSt.setString(2,payload);
            prSt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}

