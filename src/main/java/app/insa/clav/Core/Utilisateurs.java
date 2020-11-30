package app.insa.clav.Core;

import java.io.Serializable;
import java.util.Date;
import java.net.*;


//Classe qui permet d'identifier un utilisateur

public class Utilisateurs implements Comparable{
    private String pseudo;
    private InetAddress inetAddress;
    private int id;
    private int port;

    public Date getLatestUpdate() {
        return latestUpdate;
    }

    public void setLatestUpdate(Date latestUpdate) {
        this.latestUpdate = latestUpdate;
    }

    private Date latestUpdate;

    public Utilisateurs(String pseudo, InetAddress inetAddress, int id, int port) {
        this.pseudo = pseudo;
        this.inetAddress = inetAddress;
        this.id = id;
        this.port = port;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPseudo() {
        return pseudo;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getId() {
        return id;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString(){
        return Integer.toString(this.id) + "|" + this.pseudo + "|" + this.inetAddress.toString() + "|" + Integer.toString(this.port);
    }


    @Override
    public boolean equals(Object obj) {
        Utilisateurs aux = (Utilisateurs) obj;
        if (this.id == aux.id){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public int compareTo(Object o) {
        Utilisateurs u = (Utilisateurs)  o;
        return this.pseudo.compareTo(u.pseudo);
    }
}
