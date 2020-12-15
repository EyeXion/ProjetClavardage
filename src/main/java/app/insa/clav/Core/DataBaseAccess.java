package app.insa.clav.Core;

import app.insa.clav.Messages.Message;
import app.insa.clav.Messages.MessageHistoryList;

import java.sql.*;
import java.util.ArrayList;

public class DataBaseAccess {

    /**
     * Instance of the singleton class
     */
    private static DataBaseAccess instance = null;

    /**
     * Used to connect to the DB
     */
    public Connection con;

    private DataBaseAccess() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://srv-bdens.insa-toulouse.fr:3306/tp_servlet_013?useSSL=false", "tp_servlet_013", "eiN3ahng");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /** Used to get Instance/Create Instance of DBAccess if necessary
     * @return Singleton Instance of DB
     */
    public static DataBaseAccess getInstance() {
        synchronized (DataBaseAccess.class) {
            DataBaseAccess res = instance;
            if (res == null) {
                res = new DataBaseAccess();
            }
            return res;
        }
    }

    /**
     * Returns the pseudo of the user that has the login in parameters
     * @param login login of the user
     * @return pseudo of the user
     */
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

    /**
     * Checks if a user with the login is already created or not
     * @param login login of user
     * @return true if already exists, else false
     */
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

    /** Gets id of user identified by the login
     * @param login
     * @return id of user
     */
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

    /**Add an user to the DB
     * @param login login of user
     * @param pseudo pseudo of user
     * @return if of user
     */
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

    /** Checks if the Message table between 2 users id1 and id2 exists
     * @param id1
     * @param id2
     * @return true if table exists, else false
     */
    public boolean isTableCreated(int id1, int id2){
        int idGrand;
        int idPetit;
        boolean res = true;
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
        } catch (SQLException throwables) {
            res = false;
            throwables.printStackTrace();
        }
        System.out.println(res);
        return res;
    }

    /** Gets the message history between id1 and id2
     * @param id1
     * @param id2
     * @return list of MessageHistoryList
     */
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

    /** Ajoute un message à la DB envoyé par l'user local à l'user remote
     * @param idLocal
     * @param idRemote
     * @param payload
     */
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

    /** Create a message Table between id1 and id2
     * @param id1
     * @param id2
     */
    public void createChatTable(int id1, int id2){
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
        String preparedQuery = "CREATE TABLE `" +  nomTable +"` (\n" + "`id` int NOT NULL,\n" + "  `sourceId` int NOT NULL,\n" +  "  `date` timestamp NOT NULL,\n" +  "  `payload` mediumtext NOT NULL\n" +  ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
        PreparedStatement prSt = null;
        System.out.println(preparedQuery);
        try {
            prSt = con.prepareStatement(preparedQuery);
            prSt.executeUpdate();
            preparedQuery = "\nALTER TABLE `"+ nomTable + "`\n" + "  ADD PRIMARY KEY (`id`);";
            prSt = con.prepareStatement(preparedQuery);
            prSt.executeUpdate();
            preparedQuery = "\nALTER TABLE `" + nomTable + "`\n" + "  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;";
            prSt = con.prepareStatement(preparedQuery);
            prSt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /** Updates the pseudo of the user in the DB
     * @param id
     * @param pseudo
     */
    public void updatePseudo(int id, String pseudo){
        String preparedQuery = "UPDATE `Utilisateurs` SET `pseudo`=? WHERE id=?";
        PreparedStatement prSt = null;
        try {
            prSt = con.prepareStatement(preparedQuery);
            prSt.setString(1, pseudo);
            prSt.setInt(2,id);
            prSt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}

