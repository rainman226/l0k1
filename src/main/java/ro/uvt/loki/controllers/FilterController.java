package ro.uvt.loki.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.opencv.core.Mat;
import ro.uvt.loki.services.FilterService;
import ro.uvt.loki.services.StateService;

import static ro.uvt.loki.HelperFunctions.noImageSelectedAlert;
import static ro.uvt.loki.HelperFunctions.showAlert;

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
    public void blurImage() {
        //TODO change to slider that goes from 2 to 2
            if(!stateService.isImageLoaded()) {
                noImageSelectedAlert();
                return;
            }

            double size = 1.0;

            try {
                size = Double.parseDouble(inputBlur.getText());
            } catch (NumberFormatException e) {
                showAlert("Invalid input", "The size must be a number");
            }

            if(size < 0 || size > 99) {
                showAlert("Invalid input", "The size must be between 0 and 100");
                return;
            }

            Mat processedImage = stateService.getProcessedImage();
            Mat transformedImage = filterService.gaussianBlur(processedImage, size);
            stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void medianFilter() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        int ksize = 1;
        try {
            ksize = Integer.parseInt(inputKsize.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid input", "The ksize must be a number");
        }

        if(ksize < 0 || ksize > 99) {
            showAlert("Invalid input", "The ksize must be between 0 and 100");
            return;
        }

        if(ksize % 2 == 0) {
            ksize -= 1;
        }
        System.out.println(ksize);
        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = filterService.medianFilter(processedImage, ksize);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void sharpen() {
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
            showAlert("Invalid input", "The radius and amount must be numbers");
        }

        if(radius < 0 || radius > 99 || amount < 0 || amount > 99) {
            showAlert("Invalid input", "The radius and amount must be between 0 and 100");
            return;
        }

        // Workaround for the radius being even
        if(radius % 2 == 0) {
            radius -= 1;
        }

        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = filterService.sharpen(processedImage, radius, amount);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void applyBilateralFilter() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = filterService.applyBilateralFilter(processedImage);
        stateService.setProcessedImage(transformedImage);
    }
}
