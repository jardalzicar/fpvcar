package carcontrol;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Controller {

    private RemoteControl remoteControl;
    private VideoStream videoStream;
    private Joystick joystick;

    private Thread remoteControlThread;
    private Thread joystickThread;
    private Thread videoStreamThread;


    public void startRemoteControl(Socket socket){
        remoteControl = new RemoteControl(socket);

        Task<Void> task = new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                remoteControl.run();
                return null;
            }
        };

        remoteControlThread = new Thread(task);
        remoteControlThread.start();
    }

    public void stopRemoteControl(){
        remoteControl.stop();
        try {
            remoteControlThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startJoystick(Socket socket){
        joystick = new Joystick(socket);

        Task<Void> task = new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                joystick.run();
                return null;
            }
        };

        joystickThread = new Thread(task);
        joystickThread.start();
    }

    public void stopJoystick(){
        joystick.stop();
        try {
            joystickThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startVideoStream(){
        Task<Void> task = new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                try {
                    Process p = Runtime.getRuntime().exec("lib/stream.sh");
                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;
                    while ((line = br.readLine()) != null){
                        System.out.println(line+'\n');
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public void stopVideoStream(){

    }

    public void stopAll(){
        stopRemoteControl();
        startVideoStream();
    }



    public RemoteControl getRemoteControl() {
        return remoteControl;
    }

    public void setRemoteControl(RemoteControl remoteControl) {
        this.remoteControl = remoteControl;
    }

    public VideoStream getVideoStream() {
        return videoStream;
    }

    public void setVideoStream(VideoStream videoStream) {
        this.videoStream = videoStream;
    }
}
