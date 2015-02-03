package scripts.MassFighter.GUI;

import com.runemate.game.api.hybrid.Environment;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scripts.MassFighter.MassFighter;

import java.io.InputStream;

public class Main extends Stage {

    public static Controller controller;
    public static Stage stage;

    public Main() {
        try {
            start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(Stage stage) throws Exception {
        InputStream input = MassFighter.class.getResourceAsStream("/scripts/MassFighter/GUI/FighterGUI.fxml");
        if (input != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setController(new Controller());
            Parent root = loader.load(input);
            Scene scene = new Scene(root);
            stage.setTitle("MassFighter");
            stage.setScene(scene);
            Main.stage = stage;
            Main.controller = loader.getController();
            controller.initialize();
            stage.setOnCloseRequest(event -> {
                System.out.println("UI Closed - Stopping Script");
                if (Environment.getScript() != null && Environment.getScript().isRunning()) {
                    Environment.getScript().stop();
                }
                stage.close();
            });
            stage.show();
        } else {
            MassFighter.status = "GUI Fail";
        }
    }

}
