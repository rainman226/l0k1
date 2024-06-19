package ro.uvt.loki.controllers;

import javafx.event.ActionEvent;
import org.opencv.core.Mat;
import ro.uvt.loki.services.RestorationService;
import ro.uvt.loki.services.StateService;

import static ro.uvt.loki.HelperFunctions.noImageSelectedAlert;

public class RestorationController {
    private final StateService stateService = StateService.getInstance();
    private final RestorationService restorationService = new RestorationService();

    public void inpaintImageMaskSelected(ActionEvent event) {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = restorationService.inpaintImageMaskSelected(processedImage);
        stateService.setProcessedImage(transformedImage);
    }

    public void inpaintImageMaskComputed(ActionEvent event) {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = restorationService.inpaintImageComputeMask(processedImage);
        stateService.setProcessedImage(transformedImage);
    }

    public void applyCLAHE(ActionEvent event) {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = restorationService.applyCLAHE(processedImage);
        stateService.setProcessedImage(transformedImage);
    }

    public void adaptiveThresholding(ActionEvent event) {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = restorationService.applyAdaptiveThreshold(processedImage);
        stateService.setProcessedImage(transformedImage);
    }
}
