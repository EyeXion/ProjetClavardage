package app.insa.clav.Core;

import java.net.*;
import java.util.Date;


//Classe qui permet d'identifier un utilisateur

public class Utilisateurs implements Comparable{
    private String pseudo;
    private InetAddress inetAddress;
    private int id;
    private String login;
    private boolean isOutdoor;
    private Date lastUpdate;
    private int tcpListeningPort;


    public Utilisateurs(String pseudo, InetAddress inetAddress, int id, int tcpListeningPort) {
        this.pseudo = pseudo;
        this.inetAddress = inetAddress;
        this.id = id;
        this.tcpListeningPort = tcpListeningPort;
        this.isOutdoor = true;
        this.lastUpdate = new Date();
        System.out.println("Date du jour " + this.lastUpdate.toString());
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

    public void setTcpListeningPort(int tcpListeningPort) {
        this.tcpListeningPort = tcpListeningPort;
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

    public String getLogin() {
        return login;
    }

    public boolean isOutdoor() {
        return isOutdoor;
    }

    public void setOutdoor(boolean outdoor) {
        isOutdoor = outdoor;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getTcpListeningPort() {
        return tcpListeningPort;
    }

    public void update() {
        this.lastUpdate = new Date();
    }

    public boolean userToOld(Date date) {
        System.out.println("Local Date : " + this.lastUpdate.toString());
        System.out.println("Local time : " + this.lastUpdate.getTime());
        System.out.println("Remote Date : " + date.toString());
        System.out.println("Remote Time : " + date.getTime());
        return (date.getTime() - this.lastUpdate.getTime() > 4000);
    }

    @Override
    public String toString() {
        return "Utilisateurs{" +
                "pseudo='" + pseudo + '\'' +
                ", inetAddress=" + inetAddress +
                ", id=" + id +
                ", login='" + login + '\'' +
                ", isOutdoor=" + isOutdoor +
                ", lastUpdate=" + lastUpdate +
                ", tcpListeningPort=" + tcpListeningPort +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        Utilisateurs aux = (Utilisateurs) obj;
        return this.id == aux.id;
    }

    @Override
    public int compareTo(Object o) {
        Utilisateurs u = (Utilisateurs)  o;
        return this.pseudo.compareTo(u.pseudo);
    }
}
