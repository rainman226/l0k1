package ro.uvt.loki;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Loader.load(opencv_java.class);
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/main.css")).toExternalForm());
        stage.setTitle("Loki");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}