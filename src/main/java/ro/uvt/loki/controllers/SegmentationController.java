package ro.uvt.loki.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.opencv.core.Mat;
import ro.uvt.loki.services.SegmentationService;
import ro.uvt.loki.services.StateService;

public class SegmentationController {
    private final StateService stateService = StateService.getInstance();
    private final SegmentationService segmentationService = new SegmentationService();

    @FXML
    public void watershedSegmentation(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = segmentationService.applyWatershed(processedImage);
        stateService.setProcessedImage(transformedImage);
    }
}