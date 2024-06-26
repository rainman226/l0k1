package ro.uvt.loki.controllers.algorithms;

import org.opencv.core.Mat;
import ro.uvt.loki.services.RestorationService;
import ro.uvt.loki.services.StateService;

import static ro.uvt.loki.HelperFunctions.noImageSelectedAlert;

public class RestorationController {
    private final StateService stateService = StateService.getInstance();
    private final RestorationService restorationService = new RestorationService();

    public void inpaintImageMaskSelected() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = restorationService.inpaintImageMaskSelected(processedImage);
        stateService.setProcessedImage(transformedImage);
    }

    public void inpaintImageMaskComputed() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = restorationService.inpaintImageComputeMask(processedImage);
        stateService.setProcessedImage(transformedImage);
    }

    public void applyCLAHE() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = restorationService.applyCLAHE(processedImage);
        stateService.setProcessedImage(transformedImage);
    }

    public void adaptiveThresholding() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = restorationService.applyAdaptiveThreshold(processedImage);
        stateService.setProcessedImage(transformedImage);
    }
}
