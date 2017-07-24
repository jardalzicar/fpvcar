package carcontrol;

/**
 * Created by jaroslavlzicar on 13/09/16.
 */
public class Joystick {

    private Socket socket;
    boolean stopped = false;
    int period = 20;

    public Joystick(Socket socket) {
        this.socket = socket;
        System.out.println("joystick started");
    }

    public void run(){
        while(!stopped){
            System.out.println("receiving");
            socket.receiveValues();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop(){
        stopped = true;
    }
}
