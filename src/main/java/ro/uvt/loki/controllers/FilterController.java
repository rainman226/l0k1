package ro.uvt.loki.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.opencv.core.Mat;
import ro.uvt.loki.services.FilterService;
import ro.uvt.loki.services.StateService;

import static ro.uvt.loki.HelperFunctions.noImageSelectedAlert;

public class FilterController {
    @FXML
    private TextField inputBlur;

    @FXML
    private TextField inputSharpenRadius;

    @FXML
    private TextField inputSharpenAmount;

    @FXML
    private TextField inputKsize;
    private final StateService stateService = StateService.getInstance();
    private final FilterService filterService = new FilterService();

    @FXML
    public void blurImage(ActionEvent event) {
            if(!stateService.isImageLoaded()) {
                noImageSelectedAlert();
                return;
            }

            double size = 1.0;

            try {
                size = Double.parseDouble(inputBlur.getText());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for the size");
            }

            Mat processedImage = stateService.getProcessedImage();
            Mat transformedImage = filterService.gaussianBlur(processedImage, size);
            stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void medianFilter(ActionEvent event) {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        int ksize = 1;
        try {
            ksize = Integer.parseInt(inputKsize.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for the ksize");
        }
        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = filterService.medianFilter(processedImage, ksize);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void sharpen(ActionEvent event) {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        double radius = 0.0;
        double amount = 0.0;

        try {
            radius = Double.parseDouble(inputSharpenRadius.getText());
            amount = Double.parseDouble(inputSharpenAmount.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for the amount/radius");
        }

        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = filterService.sharpen(processedImage, radius, amount);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void applyBilateralFilter(ActionEvent event) {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = filterService.applyBilateralFilter(processedImage);
        stateService.setProcessedImage(transformedImage);
    }
}
