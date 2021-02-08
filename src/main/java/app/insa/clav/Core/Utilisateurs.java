package app.insa.clav.Core;

import java.net.*;


//Classe qui permet d'identifier un utilisateur

public class Utilisateurs implements Comparable{
    private String pseudo;
    private InetAddress inetAddress;
    private int id;
    private String login;
    private boolean isOutdoor;

    private int tcpListeningPort;


    public Utilisateurs(String pseudo, InetAddress inetAddress, int id, int tcpListeningPort) {
        this.pseudo = pseudo;
        this.inetAddress = inetAddress;
        this.id = id;
        this.tcpListeningPort = tcpListeningPort;
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

    @Override
    public String toString() {
        return "Utilisateurs{" +
                "pseudo='" + pseudo + '\'' +
                ", inetAddress=" + inetAddress +
                ", id=" + id +
                ", login='" + login + '\'' +
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
