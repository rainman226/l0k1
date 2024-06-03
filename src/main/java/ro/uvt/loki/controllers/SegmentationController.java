package ro.uvt.loki.controllers;

import javafx.event.ActionEvent;
import org.opencv.core.Mat;
import ro.uvt.loki.services.SegmentationService;
import ro.uvt.loki.services.StateService;

public class SegmentationController {
    private StateService stateService;
    private final SegmentationService segmentationService = new SegmentationService();

    public void setStateService(StateService stateService) {
        this.stateService = stateService;
    }

    public void watershedSegmentation(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = segmentationService.applyWatershed(processedImage);
        stateService.setProcessedImage(transformedImage);
    }
}