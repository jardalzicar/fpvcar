package carcontrol;

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

    public Socket(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void sendValues(int throttle, int steering){
        String message = steering + " " + throttle;
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
