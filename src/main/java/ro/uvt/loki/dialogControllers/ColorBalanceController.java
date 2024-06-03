package ro.uvt.loki.dialogControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

public class ColorBalanceController {
    @FXML
    private Slider redGainSlider;

    @FXML
    private Slider greenGainSlider;

    @FXML
    private Slider blueGainSlider;

    private Stage dialogStage;
    private boolean okClicked = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public float getRedGain() {
        return (float) redGainSlider.getValue();
    }

    public float getGreenGain() {
        return (float) greenGainSlider.getValue();
    }

    public float getBlueGain() {
        return (float) blueGainSlider.getValue();
    }

    @FXML
    private void handleOk() {
        okClicked = true;
        dialogStage.close();
    }
}
