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

    private InetAddress addrBroadcast;
    private int portListening;

    /**
     * Constructeur
     * @param addrBroadcast
     *                  IP reseau
     * @param localAddress
     *                  Adresse local sur le reseau
     * @param portListening
     *                  Port de destination
     */
    public UDPOutput(InetAddress addrBroadcast,InetAddress localAddress, int portListening){
        try {
            this.socket = new DatagramSocket(0, localAddress);
            this.socket.setBroadcast(true);
            this.portListening = portListening;
            this.addrBroadcast = addrBroadcast;
        }
        catch (SocketException e){
            System.out.println("Exception creation SocketDatagrammeConfiguration");
            e.printStackTrace();
        }
    }

    /**
     * Envoie le message en paramètre
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
            DatagramPacket packet = new DatagramPacket(buffer,buffer.length,this.addrBroadcast,this.portListening);
            System.out.println(packet.toString());
            System.out.println(this.addrBroadcast.toString());
            this.socket.send(packet);
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
