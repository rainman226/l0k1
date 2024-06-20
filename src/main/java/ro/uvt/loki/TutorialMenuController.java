package ro.uvt.loki;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ro.uvt.loki.controllers.EnchantmentController;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class TutorialMenuController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private AnchorPane tutorialMenuContainer;

    private void loadFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane newLoadedPane = loader.load();
            tutorialMenuContainer.getChildren().clear();
            tutorialMenuContainer.getChildren().add(newLoadedPane);

            // Get the controller instance
            Object controller = loader.getController();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToMain(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.setScene(new Scene(root));

            MainController mainController = loader.getController();
            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/main.css")).toExternalForm());

        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception
        }
    }

    @FXML
    public void loadEnchantment() {
        loadFXML("tutorial views/EnhancementTutorialsView.fxml");
    }
}
