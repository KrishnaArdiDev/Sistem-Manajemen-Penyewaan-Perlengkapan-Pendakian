// Ini buat inti skema aplikasinya dijalanin, file Main, sama kek file web.php kalo laravel

package com.sistemmanajemenpenyewaanperlengkapanpendakian;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("main-layout"), 1440, 900);
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        String path = "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/"
                + fxml + ".fxml";

        URL resource = App.class.getResource(path);

        if (resource == null) {
            throw new IOException("File FXML tidak ditemukan: " + path);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(resource);

        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
