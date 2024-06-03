package ro.uvt.loki.controllers;

import javafx.event.ActionEvent;
import org.opencv.core.Mat;
import ro.uvt.loki.services.EdgeDetectionService;
import ro.uvt.loki.services.StateService;

public class EdgeDetectionController {
    private final StateService stateService = StateService.getInstance();
    private final EdgeDetectionService edgeDetectionService = new EdgeDetectionService();

    public void sobelEdgeDetection(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        //TODO: add input for amount
        Mat transformedImage = edgeDetectionService.sobel(processedImage, 2);
        stateService.setProcessedImage(transformedImage);
    }

    public void prewittEdgeDetection(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        //TODO: add input for amount
        Mat transformedImage = edgeDetectionService.prewitt(processedImage, 10);
        stateService.setProcessedImage(transformedImage);
    }

    public void robertsEdgeDetection(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        //TODO: add input for amount
        Mat transformedImage = edgeDetectionService.robertsCross(processedImage, 2);
        stateService.setProcessedImage(transformedImage);
    }

    public void differenceOfGaussians(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        //TODO: add input for amount
        Mat transformedImage = edgeDetectionService.differenceOfGaussians(processedImage, 2, 4, 1);
        stateService.setProcessedImage(transformedImage);
    }
}

