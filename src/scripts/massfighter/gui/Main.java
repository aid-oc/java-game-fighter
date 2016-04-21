package scripts.massfighter.gui;

import com.runemate.game.api.client.embeddable.EmbeddableUI;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.util.Resources;
import com.runemate.game.api.script.framework.AbstractScript;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scripts.massfighter.MassFighter;

import java.io.IOException;
import java.io.InputStream;

public class Main implements EmbeddableUI {

    private static Controller controller;

    private final AbstractScript bot;

    public Main(AbstractScript bot) {
        this.bot = bot;
    }

    private void start(Stage stage) throws Exception {
        InputStream input = Resources.getAsStream("scripts/massfighter/gui/FighterGUI-V2.fxml");
        if (input != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setController(new Controller(bot));
            Parent root = loader.load(input);
            Scene scene = new Scene(root);
            stage.setTitle("MassFighter");
            stage.setScene(scene);
            //Main.stage = stage;
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

    @Override
    public ObjectProperty<? extends Node> botInterfaceProperty() {
        if (botInterfaceProperty == null) {
            InputStream input = Resources.getAsStream("scripts/massfighter/gui/FighterGUI-V2.fxml");
            if (input != null) {
                FXMLLoader loader = new FXMLLoader();
                loader.setController(new Controller(bot));
                try {
                    Parent root = loader.load(input);
                    botInterfaceProperty = new SimpleObjectProperty<>(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return botInterfaceProperty;
    }

    private ObjectProperty<? extends Node> botInterfaceProperty;
}
