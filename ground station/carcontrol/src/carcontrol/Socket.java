package carcontrol;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;

/**
 * Created by jaroslavlzicar on 22/07/16.
 */
public class Socket {

    String host;
    int port;
    InetAddress address;
    DatagramSocket socket;
    byte[] buffer;

    public Socket(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            socket = new DatagramSocket(port);
            socket.setSoTimeout(2000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void sendValues(int throttle, int steering){
        String message = steering + " " + throttle+"\n";
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), address, port);
        try {
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveValues(){
        buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(packet.getAddress().getHostName());
        System.out.println(packet.getPort());

        String msg = new String(buffer, 0, packet.getLength());
        //String[] splitted = msg.split("\\s+");

        System.out.println(msg);
    }


}
