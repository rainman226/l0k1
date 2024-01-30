package ro.uvt.loki;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class TutorialMenuController implements Initializable {
    @FXML
    private ListView<String> listTutorials;

    @FXML
    private Label textLabel;

    String[] tutorials = {"Enchantment", "Image restoration", "Space conversion", "Segmentation", "Compression"};
    String currentTutorial;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listTutorials.getItems().addAll(tutorials);

        listTutorials.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                currentTutorial = listTutorials.getSelectionModel().getSelectedItem();
                textLabel.setText(currentTutorial);
            }
        });
    }
}
