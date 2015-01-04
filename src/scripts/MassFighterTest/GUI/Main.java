package scripts.MassFighterTest.GUI;

import com.runemate.game.api.hybrid.Environment;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import scripts.MassFighterTest.MassFighterTest;

public class Main extends Application {

    private Controller controller = new Controller();

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(MassFighterTest.class.getResource("GUI/FighterDesign.fxml"));
        Pane root = loader.load();
        primaryStage.setTitle("MassFighterTest V4");
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("UI Closed - Stopping Script");
            if (Environment.getScript().isRunning()) {
                Environment.getScript().stop();
            }
            close();
        });
        primaryStage.show();
        if (loader.getController() != null) {
            controller = loader.getController();
            controller.initialize();
        } else {
            System.out.println("Failed to locate Controller");
        }



        /*
        InputStream in = MassFighterTest.class.getResourceAsStream("/scripts/MassFighterTest/GUI/FighterDesign.fxml");
        if (in != null) {
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(in);
            primaryStage.setTitle("MassFighterTest V4");
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
        } else {
            MassFighterTest.status = "Input Stream Unavailable";
        } */
    }

    public void close() {
        controller.closeUI();
    }

}
