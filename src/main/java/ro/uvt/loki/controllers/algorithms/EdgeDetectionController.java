package ro.uvt.loki.controllers.algorithms;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import org.opencv.core.Mat;
import ro.uvt.loki.services.EdgeDetectionService;
import ro.uvt.loki.services.StateService;

import static ro.uvt.loki.HelperFunctions.noImageSelectedAlert;
import static ro.uvt.loki.HelperFunctions.showAlert;

public class EdgeDetectionController {
    @FXML
    private Slider sliderIntensityScale;

    @FXML
    private TextField dogR1;

    @FXML
    private TextField dogR2;
    private final StateService stateService = StateService.getInstance();
    private final EdgeDetectionService edgeDetectionService = new EdgeDetectionService();

    @FXML
    public void sobelEdgeDetection() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        int amount = 1;

        try {
            amount = (int) sliderIntensityScale.getValue();
        } catch (Exception e) {
            // something
        }
        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = edgeDetectionService.sobel(processedImage, amount);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void prewittEdgeDetection() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        int amount = 1;

        try {
            amount = (int) sliderIntensityScale.getValue();
        } catch (Exception e) {
            // something
        }
        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = edgeDetectionService.prewitt(processedImage, amount);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void robertsEdgeDetection() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        int amount = 1;

        try {
            amount = (int) sliderIntensityScale.getValue();
        } catch (Exception e) {
            // something
        }
        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = edgeDetectionService.robertsCross(processedImage, amount);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void differenceOfGaussians() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        double radius1, radius2;

        try {
            radius1 = Double.parseDouble(dogR1.getText());
            radius2 = Double.parseDouble(dogR2.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid input", "The radius must be a number");
            return;
        }

        if(radius1 < 0 || radius1 > 99 || radius2 < 0 || radius2 > 99) {
            showAlert("Invalid input", "The radius must be between 0 and 100");
            return;
        }

        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = edgeDetectionService.differenceOfGaussians(processedImage, radius1, radius2);
        stateService.setProcessedImage(transformedImage);
    }
}

