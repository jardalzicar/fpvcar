package carcontrol;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class Main extends Application {
    Slider steeringSlider;
    Slider throttleSlider;
    Button videoButton;
    Button controlButton;
    Button joystickButton;
    BorderPane pane;
    Scene scene;
    Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        controller = new Controller();
        initGUI();

        scene = new Scene(pane, 600, 400);
        primaryStage.setTitle("RC car control");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void initGUI(){
        pane = new BorderPane();
        pane.setPadding(new Insets(20,20,20,20));

        throttleSlider = new Slider(0,180,90);
        throttleSlider.setOrientation(Orientation.VERTICAL);
        throttleSlider.setPadding(new Insets(20,0,20,25));
        throttleSlider.setShowTickLabels(true);
        throttleSlider.setShowTickMarks(true);
        throttleSlider.setMajorTickUnit(90);
        throttleSlider.setMinorTickCount(9);
        throttleSlider.setDisable(true);
        throttleSlider.setFocusTraversable(false);
        pane.setCenter(throttleSlider);

        steeringSlider = new Slider(0,180,90);
        steeringSlider.setShowTickLabels(true);
        steeringSlider.setShowTickMarks(true);
        steeringSlider.setMajorTickUnit(90);
        steeringSlider.setMinorTickCount(9);
        steeringSlider.setDisable(true);
        steeringSlider.setFocusTraversable(false);
        pane.setBottom(steeringSlider);

        HBox hBox = new HBox(10);
        videoButton = new Button("Video stream");
        videoButton.setOnAction(event -> {
            controller.startVideoStream();
        });

        controlButton = new Button("Remote control");
        controlButton.setOnAction(event -> {
            controller.startRemoteControl(new Socket("raspberrypi.local", 8888));
            setupRemoteControl();
        });

        joystickButton = new Button("Joystick");
        joystickButton.setOnAction(event -> {
            controller.startJoystick(new Socket("localhost", 8000));
            //setupRemoteControl();
        });

        hBox.getChildren().addAll(videoButton,controlButton,joystickButton);
        pane.setTop(hBox);
    }

    public void setupRemoteControl(){
        throttleSlider.setDisable(false);
        steeringSlider.setDisable(false);
        throttleSlider.valueProperty().bindBidirectional(controller.getRemoteControl().throttle);
        steeringSlider.valueProperty().bindBidirectional(controller.getRemoteControl().steering);
        scene.setOnKeyPressed(new KeyPressedEventHandler());
        scene.setOnKeyReleased(new KeyReleasedEventHandler());
    }

    private class KeyPressedEventHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()){
                case UP: controller.getRemoteControl().throttleUp(); break;
                case DOWN: controller.getRemoteControl().throttleDown();
                    break;
                case RIGHT: controller.getRemoteControl().steeringLeft();
                    break;
                case LEFT: controller.getRemoteControl().steeringRight(); break;
            }
        }
    }

    private class KeyReleasedEventHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()){
                case UP:
                case DOWN: controller.getRemoteControl().stopThrottle(); break;
                case LEFT:
                case RIGHT:controller.getRemoteControl().stopSteering(); break;
            }
        }
    }



}
