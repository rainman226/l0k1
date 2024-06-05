package ro.uvt.loki.controllers;

import javafx.event.ActionEvent;
import org.opencv.core.Mat;
import ro.uvt.loki.services.FilterService;
import ro.uvt.loki.services.RestorationService;
import ro.uvt.loki.services.StateService;

import javax.swing.*;

public class RestorationController {
    private final StateService stateService = StateService.getInstance();
    private final RestorationService restorationService = new RestorationService();

    public void inpaintImageMaskSelected(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = restorationService.inpaintImageMaskSelected(processedImage);
        stateService.setProcessedImage(transformedImage);
    }

    public void inpaintImageMaskComputed(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = restorationService.inpaintImageComputeMask(processedImage);
        stateService.setProcessedImage(transformedImage);
    }

    public void applyCLAHE(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = restorationService.applyCLAHE(processedImage);
        stateService.setProcessedImage(transformedImage);
    }

    public void adaptiveThresholding(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = restorationService.applyAdaptiveThreshold(processedImage);
        stateService.setProcessedImage(transformedImage);
    }
}
