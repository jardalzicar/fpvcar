package carcontrol;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Controller {

    int defaultThrottle = 90;
    int minThrottle = 0;
    int maxThrottle = 180;
    int throttleIncrement = 0;

    int defaultSteering = 90;
    int minSteering = 0;
    int maxSteering = 180;
    int steeringIncrement = 0;

    public IntegerProperty steering = new SimpleIntegerProperty(defaultSteering);
    public IntegerProperty throttle = new SimpleIntegerProperty(defaultThrottle);

    int period = 60;
    int frequency = 1000/ period;

    public Controller() {

    }

    public void run(){
        while (true){
            processValues();
            sendValues();
            try {
                Thread.sleep(period);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void processValues(){
        throttle.setValue(throttle.get() + throttleIncrement);
        steering.setValue(steering.get() + steeringIncrement);

        if(throttle.get()  >= maxThrottle){
            throttle.setValue(maxThrottle);
            throttleIncrement = 0;
        }
        if(throttle.get()  <= minThrottle){
            throttle.setValue(minThrottle);
            throttleIncrement = 0;
        }
        if(steering.get() >= maxSteering){
            steering.setValue(maxSteering);
            steeringIncrement = 0;
        }
        if(steering.get() <= minSteering){
            steering.setValue(minSteering);
            steeringIncrement = 0;
        }

        System.out.println(steering.get()+" "+throttle.get());
    }



    public void throttleUp(){
        throttleIncrement = 2;
    }

    public void throttleDown(){
        throttleIncrement = -2;
    }

    public void stopThrottle(){
        throttle.setValue(defaultThrottle);
        throttleIncrement = 0;
    }

    public void steeringLeft(){
        steeringIncrement = -2;
    }

    public void steeringRight(){
        steeringIncrement = 2;
    }

    public void stopSteering(){
        steering.setValue(defaultSteering);
        steeringIncrement = 0;
    }

    private void sendValues() {
    }

}