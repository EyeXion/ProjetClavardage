package app.insa.clav.Reseau;
import app.insa.clav.Core.Utilisateurs;
import app.insa.clav.Messages.MessagePseudo;

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

    public ServletConnection(){
        builder = new GsonBuilder();
        gson = builder.create();;
        //baseURL = "https://srv-gei-tomcat.insa-toulouse.fr/ServletJiggly/";
        baseURL = "http://localhost:8080/Gradle___com_example___ServeurClavardage_1_0_SNAPSHOT_war/";
    }

    public static ServletConnection getInstance(){
        synchronized(ServletConnection.class){
            if (instance == null) {
                instance = new ServletConnection();
            }
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
            //resList = new Utilisateurs[Integer.parseInt(con.getHeaderField("sizeArray"))];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resList;
    }

    public void submitConnectionIndoor(Utilisateurs user){
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
}
