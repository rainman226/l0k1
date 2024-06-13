package ro.uvt.loki.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.opencv.core.Mat;
import ro.uvt.loki.services.OthersService;
import ro.uvt.loki.services.StateService;

public class OthersController {
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
}