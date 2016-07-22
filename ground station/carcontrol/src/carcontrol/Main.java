package carcontrol;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
    Slider stearingSlider;
    Slider throttleSlider;
    BorderPane pane;
    Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        controller = new Controller();

        Task<Void> task = new Task<Void>(){
            @Override
            protected Void call() throws Exception {

                controller.run();
                return null;
            }
        };
        new Thread(task).start();

        initGUI();

        Scene scene = new Scene(pane, 600, 400);
        scene.setOnKeyPressed(new KeyPressedEventHandler());
        scene.setOnKeyReleased(new KeyReleasedEventHandler());

        primaryStage.setTitle("RC car control");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
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
        pane.setCenter(throttleSlider);

        stearingSlider = new Slider(0,180,90);
        stearingSlider.setShowTickLabels(true);
        stearingSlider.setShowTickMarks(true);
        stearingSlider.setMajorTickUnit(90);
        stearingSlider.setMinorTickCount(9);
        stearingSlider.setDisable(true);
        pane.setBottom(stearingSlider);

        throttleSlider.valueProperty().bindBidirectional(controller.throttle);
        stearingSlider.valueProperty().bindBidirectional(controller.steering);

        // Also works ;)
        /*
        controller.steering.addListener((observable, oldValue, newValue) -> {
            stearingSlider.setValue((int) newValue);
            System.out.println("steering value changed");
        });
        */
    }

    private class KeyPressedEventHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()){
                case UP: controller.throttleUp(); break;
                case DOWN: controller.throttleDown(); break;
                case LEFT: controller.steeringLeft(); break;
                case RIGHT: controller.steeringRight(); break;
            }
        }
    }

    private class KeyReleasedEventHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()){
                case UP:
                case DOWN: controller.stopThrottle(); break;
                case LEFT:
                case RIGHT:controller.stopSteering(); break;
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
