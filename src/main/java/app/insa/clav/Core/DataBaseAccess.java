package app.insa.clav.Core;

import app.insa.clav.Messages.MessageDisplay;
import app.insa.clav.Messages.MessageDisplayFile;

import java.io.*;
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

    private DataBaseAccess(String addr, String user, String mdp) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Erreur lors de la récupération de la classe dans DataBaseAccess");
        }
        try {
            String urlBdd = "jdbc:mysql://" + addr + ":3306/testDBChat?useSSL=false";
            //System.out.println("Tentative de connection à : " + urlBdd);
            con = DriverManager.getConnection(urlBdd, user, mdp);
            //con = DriverManager.getConnection("jdbc:mysql://srv-bdens.insa-toulouse.fr:3306/tp_servlet_013?useSSL=false", "tp_servlet_013", "eiN3ahng");
        } catch (SQLException throwables) {
            System.out.println("Erreur lors de la connexion à la BDD");
        }
        //System.out.println(con.toString());
    }

    /** Used to get Instance/Create Instance of DBAccess if necessary
     * @return Singleton Instance of DB
     */
    public static DataBaseAccess getInstance(String addr, String user, String mdp) {
        synchronized (DataBaseAccess.class) {
            if (instance == null) {
                instance = new DataBaseAccess(addr, user, mdp);
            }
            return instance;
        }
    }

    public static DataBaseAccess getInstance() {
        if (instance == null) {
            System.out.println("ATTENTION : DataBaseAccess null renvoyé");
        }
        return instance;
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
            System.out.println("Erreur lors de la requette pseudo from login avec " + login);
        }
        return pseudo;
    }

    /**
     * Checks if a user with the login is already created or not
     * @param login login of user
     * @return true if already exists, else false
     */
    public boolean isLoginUsed(String login) {
        boolean retour = false;
        String preparedQuery = "SELECT * FROM Utilisateurs WHERE login=?";
        PreparedStatement prSt = null;
        try {
            prSt = con.prepareStatement(preparedQuery);
            prSt.setString(1, login);
            ResultSet rs = prSt.executeQuery();
            retour = rs.first();
        } catch (SQLException throwables) {
            System.out.println("Erreur lors de la requette login used avec " + login);
        }
        //System.out.println("login Aux = " + loginAux);
        return retour;
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
            System.out.println("Erreur lors de la récupération de l'ID avec " + login);
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
            System.out.println("Erreur lors de l'insertion de l'utilisateur " + login);
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
            System.out.println("Erreur lors de la récupération de l'ID de l'utilisateur créé avec " + login);
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
        }
        System.out.println(res);
        return res;
    }

    /** Gets the message history between id1 and id2
     * @param id1
     * @param id2
     * @return list of MessageDisplay
     */
    public ArrayList<MessageDisplay> getMessageHistory(int id1, int id2) {
        ArrayList<MessageDisplay> history = new ArrayList<MessageDisplay>();
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
                int type;
                if ((type = rs.getInt(4)) == 1) {
                    MessageDisplay msg = new MessageDisplay();
                    msg.setSourceId(rs.getInt(2));
                    msg.setDate(rs.getString(3));
                    msg.setPayload(rs.getString(5));
                    msg.setType(rs.getInt(4));
                    history.add(msg);
                }
                else if (type == 3){
                    MessageDisplayFile msg = new MessageDisplayFile();
                    msg.setSourceId(rs.getInt(2));
                    msg.setDate(rs.getString(3));
                    msg.setType(rs.getInt(4));
                    msg.setPayload(rs.getString(7));
                    msg.setDBId(rs.getInt(1));
                    Blob blob = rs.getBlob(6);
                    InputStream is = blob.getBinaryStream();
                    String path = "file_" + idPetit + "_" + idGrand + "_" + msg.getDate() + "." + msg.getExt();
                    FileOutputStream os = new FileOutputStream(path);
                    int bytesRead = -1;
                    byte[] buffer = new byte[4096];
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    is.close();
                    os.close();
                    msg.setFile(new File(path));
                    msg.getFile().deleteOnExit();
                    history.add(msg);
                }
                else{
                    MessageDisplayFile msg = new MessageDisplayFile();
                    msg.setSourceId(rs.getInt(2));
                    msg.setDate(rs.getString(3));
                    msg.setType(rs.getInt(4));
                    msg.setPayload(rs.getString(7));
                    msg.setDBId(rs.getInt(1));
                    history.add(msg);
                }
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
        return history;
    }

    public void getFile(int DBid, File file,int id1, int id2){
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
        String preparedQuery = "SELECT * FROM " + nomTable + " WHERE id=?";
        PreparedStatement prSt = null;
        try {
            prSt = con.prepareStatement(preparedQuery);
            prSt.setInt(1,DBid);
            ResultSet rs = prSt.executeQuery();
            if (rs.next()){
                Blob blob = rs.getBlob(6);
                InputStream is = blob.getBinaryStream();
                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(file.getPath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                int bytesRead = -1;
                byte[] buffer = new byte[4096];
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                is.close();
                os.close();
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    /** Ajoute un message à la DB envoyé par l'user local à l'user remote
     * @param idLocal
     * @param idRemote
     * @param message
     */
    public void addMessage(int idLocal, int idRemote, MessageDisplay message){
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
        if (message.getType() == 1) {
            String preparedQuery = "INSERT INTO `" + nomTable + "`(`sourceId`, `date`, `payload`, `type`) VALUES (?,?,?,?)";
            PreparedStatement prSt = null;
            try {
                prSt = con.prepareStatement(preparedQuery);
                prSt.setInt(1, message.getSourceId());
                prSt.setString(2, message.getDate());
                prSt.setString(3, message.getPayload());
                prSt.setInt(4,message.getType());
                prSt.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else{
            String preparedQuery = "INSERT INTO `" + nomTable + "`(`sourceId`, `date`, `file`, `type`, `fileName`) VALUES (?,?,?,?,?)";
            PreparedStatement prSt = null;
            MessageDisplayFile msgFile = (MessageDisplayFile) message;
            try {
                prSt = con.prepareStatement(preparedQuery);
                prSt.setInt(1, message.getSourceId());
                prSt.setString(2, message.getDate());
                prSt.setBinaryStream(3, new FileInputStream(msgFile.getFile()));
                prSt.setInt(4,message.getType());
                prSt.setString(5,message.getPayload());
                prSt.executeUpdate();
            } catch (SQLException | FileNotFoundException throwables) {
                throwables.printStackTrace();
            }
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
        String preparedQuery = "CREATE TABLE `" +  nomTable +"` (\n" + "`id` int NOT NULL,\n" + "  `sourceId` int NOT NULL,\n" +  "  `date` varchar(30) NOT NULL,\n" +  " `type` int NOT NULL,\n"  + "  `payload` mediumtext,\n" + " `file` mediumblob\n,\n" + " `fileName` varchar(50) DEFAULT NULL\n" + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
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
            System.out.println("Erreur lors de la creation de la table " + nomTable);
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
            System.out.println("Erreur lors de la mise a jour du pseudo " + pseudo);
        }
    }

    public void deleteHistory(int id1, int id2) {
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
        String preparedQuery = "DROP TABLE " + nomTable;
        PreparedStatement prSt = null;
        try {
            prSt = con.prepareStatement(preparedQuery);
            prSt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}

