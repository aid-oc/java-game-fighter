package scripts.MassFighter.GUI;

import com.runemate.game.api.hybrid.Environment;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scripts.MassFighter.MassFighter;

import java.io.InputStream;

public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {

        InputStream in = MassFighter.class.getResourceAsStream("/scripts/MassFighter/GUI/FighterV1.fxml");
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(in);
        primaryStage.setTitle("MassFighter V4");
        primaryStage.setScene(new Scene(root));
        controller = loader.getController();
        controller.initialize();
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("UI Closed - Stopping Script");
            if (Environment.getScript().isRunning()) {
                Environment.getScript().stop();
            }
            close();
        });
        primaryStage.show();
    }

    public void close() {
        controller.closeUI();
    }

}
