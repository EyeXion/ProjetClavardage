package app.insa.clav.Reseau;
import app.insa.clav.Core.Utilisateurs;
import app.insa.clav.Messages.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ServletConnection {

    private static ServletConnection instance = null;

    public final GsonBuilder builder;
    public final Gson gson;
    public String baseURL;

    private ServletConnection(String urlServeur){
        builder = new GsonBuilder();
        gson = builder.create();;
        //baseURL = "https://srv-gei-tomcat.insa-toulouse.fr/ServletJiggly/";
        baseURL = urlServeur;
    }

    public static ServletConnection getInstance(String urlServeur){
        synchronized(ServletConnection.class){
            if (instance == null) {
                instance = new ServletConnection(urlServeur);
            }
        }
        return instance;
    }

    public static ServletConnection getInstance(){
        if (instance == null) {
            System.out.println("ATTENTION : Pointeur servlet null");
        }
        return instance;
    }

    public ArrayList<Utilisateurs> getRemoteActiveUsers(){
        URL url = null;
        HttpURLConnection con = null;
        ArrayList<Utilisateurs> resList = null;
        try {
            url = new URL(baseURL + "getOutdoorUsers");
        } catch (MalformedURLException e) {
            System.out.println("Erreur recup user externes actif : création URL avec " + url.toString());
            //e.printStackTrace();
        }
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            System.out.println("Error code HTTP request = " + status);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            System.out.println("Response : " + content);
            Type listType = new TypeToken<ArrayList<Utilisateurs>>(){}.getType();
            resList = gson.fromJson(content.toString(),listType);
            //resList = new Utilisateurs[Integer.parseInt(con.getHeaderField("sizeArray"))];
        } catch (IOException e) {
            System.out.println("Erreur recup user externes actif : Utilisation URL avec " + url.toString());
            e.printStackTrace();
        }
        return resList;
    }

    public void submitConnectionIndoor(Utilisateurs user){
        System.out.println("On envoi l'user au serveur avec " + user.toString());
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL(baseURL + "submitConnectionIndoor");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = gson.toJson(user).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int status = con.getResponseCode();
            System.out.println("Error code HTTP request submit indoor = " + status);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void submitConnectionOutdoor(Utilisateurs user) {
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL(baseURL + "submitConnectionOutdoor");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = gson.toJson(user).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int status = con.getResponseCode();
            System.out.println("Error code HTTP request submit outdoor = " + status);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Utilisateurs> getAllActiveUsers() {
        URL url = null;
        HttpURLConnection con = null;
        ArrayList<Utilisateurs> resList = null;
        try {
            url = new URL(baseURL + "getAllUsers");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            System.out.println("Error code HTTP request = " + status);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            System.out.println("Response : " + content);
            Type listType = new TypeToken<ArrayList<Utilisateurs>>(){}.getType();
            resList = gson.fromJson(content.toString(),listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resList;
    }

    public void submitDeconnectionOutdoor(Utilisateurs user){
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL(baseURL + "submitDeconnectionOutdoor");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = gson.toJson(user).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int status = con.getResponseCode();
            System.out.println("Error code HTTP request submit deco outdoor = " + status);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void submitDeconnectionIndoor(Utilisateurs user){
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL(baseURL + "submitDeconnectionIndoor");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = gson.toJson(user).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int status = con.getResponseCode();
            System.out.println("Error code HTTP request submit deco indoor = " + status);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SubmitMessageChatTxt(int userId, int remoteId, MessageChatTxt msg){
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL(baseURL + "SubmitMessageChat");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = gson.toJson(new MessageSrvTCP(userId, remoteId, new MessageRetourSrvTCP(msg), null)).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int status = con.getResponseCode();
            System.out.println("Error code HTTP request submit msg chat = " + status);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SubmitMessageChatFile(int userId, int remoteId, MessageChatFile msg){
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL(baseURL + "SubmitMessageChat");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = gson.toJson(new MessageSrvTCP(userId, remoteId, new MessageRetourSrvTCP(msg), null)).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int status = con.getResponseCode();
            System.out.println("Error code HTTP request submit msg chat = " + status);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SubmitMessageChat(int userId, int remoteId, Message msg){
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL(baseURL + "SubmitMessageChat");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = gson.toJson(new MessageSrvTCP(userId, remoteId, new MessageRetourSrvTCP(msg), null)).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int status = con.getResponseCode();
            System.out.println("Error code HTTP request submit msg chat = " + status);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean SubmitConnectionChat(int userId, int remoteId, MessageInit msg){
        URL url = null;
        HttpURLConnection con = null;
        boolean ret = false;
        try {
            url = new URL(baseURL + "SubmitConnectionChat");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = gson.toJson(new MessageSrvTCP(userId, remoteId, null, msg)).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int status = con.getResponseCode();
            ret = (status != 201);
            System.out.println("Error code HTTP request submit connection  avec = " + status);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public MessageRetourSrvTCP GetMessageChat(int userId, int remoteId){
        //System.out.println("Demande");
        URL url = null;
        HttpURLConnection con = null;
        MessageRetourSrvTCP resList = null;
        try {
            url = new URL(baseURL + "GetMessageChat");
        } catch (MalformedURLException e) {
            System.out.println("Erreur recup message avec " + url.toString());
            //e.printStackTrace();
        }
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);
            con.setDoInput(true);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = gson.toJson(new MessageSrvTCP(userId, remoteId, null, null)).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            System.out.println("Response : " + content);
            resList = gson.fromJson(content.toString(),MessageRetourSrvTCP.class);
            //resList = new Utilisateurs[Integer.parseInt(con.getHeaderField("sizeArray"))];
        } catch (IOException e) {
            System.out.println("Erreur recup msg chat 2 avec " + url.toString());
            e.printStackTrace();
        }
        return resList;
    }

    public ArrayList<MessageInit> GetConnectionChat(int userId){
        URL url = null;
        HttpURLConnection con = null;
        ArrayList<MessageInit> resList = null;
        try {
            url = new URL(baseURL + "GetConnectionChat");
        } catch (MalformedURLException e) {
            System.out.println("Erreur recup connecion" + url.toString());
            //e.printStackTrace();
        }
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);
            con.setDoInput(true);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = gson.toJson(new MessageSrvTCP(userId, 0, null, null)).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            System.out.println("Response : " + content);
            Type listType = new TypeToken<ArrayList<MessageInit>>(){}.getType();
            resList = gson.fromJson(content.toString(),listType);
            //resList = new Utilisateurs[Integer.parseInt(con.getHeaderField("sizeArray"))];
        } catch (IOException e) {
            System.out.println("Erreur recup connecion 2 " + url.toString());
            e.printStackTrace();
        }
        return resList;
    }
}
