package ro.uvt.loki.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import org.opencv.core.Mat;
import ro.uvt.loki.services.EdgeDetectionService;
import ro.uvt.loki.services.StateService;

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
    public void sobelEdgeDetection(ActionEvent event) {
        int amount = 1;

        try {
            amount = (int) sliderIntensityScale.getValue();
        } catch (Exception e) {
            // Handle exception
        }
        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = edgeDetectionService.sobel(processedImage, amount);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void prewittEdgeDetection(ActionEvent event) {
        int amount = 1;

        try {
            amount = (int) sliderIntensityScale.getValue();
        } catch (Exception e) {
            // Handle exception
        }
        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = edgeDetectionService.prewitt(processedImage, amount);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void robertsEdgeDetection(ActionEvent event) {
        int amount = 1;

        try {
            amount = (int) sliderIntensityScale.getValue();
        } catch (Exception e) {
            // Handle exception
        }
        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = edgeDetectionService.robertsCross(processedImage, amount);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void differenceOfGaussians(ActionEvent event) {
        double radius1, radius2;

        try {
            radius1 = Double.parseDouble(dogR1.getText());
            radius2 = Double.parseDouble(dogR2.getText());
        } catch (Exception e) {
            // Handle exception
            return;
        }
        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = edgeDetectionService.differenceOfGaussians(processedImage, radius1, radius2);
        stateService.setProcessedImage(transformedImage);
    }
}

