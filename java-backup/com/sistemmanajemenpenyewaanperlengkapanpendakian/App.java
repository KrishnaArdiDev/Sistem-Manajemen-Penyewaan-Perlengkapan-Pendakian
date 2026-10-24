
package com.sistemmanajemenpenyewaanperlengkapanpendakian;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle(
            "TAPAK - Sistem Manajemen Penyewaan Perlengkapan Pendakian"
        );

        scene = new Scene(loadFXML("main-layout"), 1440, 900);
        stage.setScene(scene);

        stage.setResizable(true);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);

        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setRoot(String fxml) throws IOException {
        if (scene == null) {
            throw new IllegalStateException(
                "Scene aplikasi belum diinisialisasi."
            );
        }

        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        String path =
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/"
            + fxml + ".fxml";

        URL resource = App.class.getResource(path);

        if (resource == null) {
            throw new IOException("File FXML tidak ditemukan: " + path);
        }

        return new FXMLLoader(resource).load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
