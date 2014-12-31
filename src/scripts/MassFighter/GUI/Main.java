package scripts.MassFighter.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FighterV1.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("MassFighter V4");
        primaryStage.setScene(new Scene(root));
        controller = loader.getController();
        controller.initialize();
        primaryStage.show();
    }

    public void close() {
        controller.closeUI();
    }

}
