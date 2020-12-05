package app.insa.clav.Reseau;

import app.insa.clav.Messages.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;


/**
 * Classe qui permet d'envoyer des messages en UDP
 */
public class UDPOutput{

    /**
     * Socket UDP par lequel on envoie les messages
     * En théorie peut être le même que celui d en Input ou sur différent port.
     */
    private DatagramSocket socket;

    /**
     * Constructeur
     * @param localAddress
     *                  IP locale
     * @param outputPort
     *                  Port du socket output
     */
    public UDPOutput(InetAddress localAddress, int outputPort){
        try {
            this.socket = new DatagramSocket(outputPort, localAddress);
            this.socket.setBroadcast(true);
        }
        catch (SocketException e){
            System.out.println("Exception creation SocketDatagrammeConfiguration");
            e.printStackTrace();
        }
    }

    /**
     * Envoie le message en paramètre (le message contient toutes les informations de routage
     * @param msg
     *          Message à envoyer
     */
    public void sendMsg(Message msg){
        try {
            //Envoi du pseudo sur le reseau local à l'adresse IP dest sur le port dest
            byte[] buffer = "".getBytes();
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
            try {
                ObjectOutputStream objectOutStream = new ObjectOutputStream(byteOutStream);
                objectOutStream.writeObject(msg);
                objectOutStream.close();
                buffer = byteOutStream.toByteArray();
            } catch (IOException e1) {
                System.out.println("Exception serialisation de l'objet envoi message");
            }
            //InetAddress broadcastAdress = InetAddress.getByAddress("255.255.255.255".getBytes());
            DatagramPacket packet = new DatagramPacket(buffer,buffer.length,msg.destIP,msg.destPort);
            System.out.println(packet.toString());
            this.socket.send(packet);
            System.out.println("UDP Packet Sended");
        }
        catch (UnknownHostException e){
            System.out.println("Unknown host dans broadcast address");
            e.printStackTrace();
        }
        catch (IOException e){
            System.out.println("IOException send pseudo");
            e.printStackTrace();
        }
    }
}
