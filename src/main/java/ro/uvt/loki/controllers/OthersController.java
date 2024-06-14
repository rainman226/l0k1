package ro.uvt.loki.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import org.opencv.core.Mat;
import ro.uvt.loki.services.OthersService;
import ro.uvt.loki.services.StateService;

public class OthersController {

    @FXML
    private Slider morphValue;
    private final StateService stateService = StateService.getInstance();
    private final OthersService othersService = new OthersService();



    @FXML
    public void watershedSegmentation(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = othersService.applyWatershed(processedImage);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void convertRGBtoHSV(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = othersService.convertRGBtoHSV(processedImage);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void applyDilation(ActionEvent event) {
        int morphValue = 1;

        try {
            morphValue = (int) this.morphValue.getValue();
        } catch (Exception e) {
            // Handle exception
        }

        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = othersService.applyDilation(processedImage, morphValue);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void applyErosion(ActionEvent event) {
        int morphValue = 1;

        try {
            morphValue = (int) this.morphValue.getValue();
        } catch (Exception e) {
            // Handle exception
        }

        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = othersService.applyErosion(processedImage, morphValue);
        stateService.setProcessedImage(transformedImage);
    }
}