package ro.uvt.loki.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import ro.uvt.loki.services.EnchantmentService;
import ro.uvt.loki.services.StateService;

import static ro.uvt.loki.HelperFunctions.*;

public class EnchantmentController {
    @FXML
    private TextField inputBrightness1;

    @FXML
    private TextField inputBrightness2;

    @FXML
    private TextField inputSaturation;

    @FXML
    private TextField inputGainRed;

    @FXML
    private TextField inputGainGreen;

    @FXML
    private TextField inputGainBlue;

    private final StateService stateService = StateService.getInstance();
    private final EnchantmentService enchantmentService = new EnchantmentService();


    private ImageView histogramImage;

    public void setHistogramImage(ImageView histogramImage) {
        this.histogramImage = histogramImage;
    }

    @FXML
    public void setHistogramImage() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        Mat processedImage = stateService.getProcessedImage();

        Mat histogramMat = EnchantmentService.calculateHistogram(processedImage);
        Mat transformedImage = enchantmentService.equaliseHistogram(processedImage);

        stateService.setProcessedImage(transformedImage);
        Image histogram = toFXImage(histogramMat);
        histogramImage.setImage(histogram);
    }

    @FXML
    public void increaseBrightness() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        double alpha = 1.0; // Default value in case the user doesn't input anything
        double beta = 1.0;  // Same ideea

        try {
            alpha = Double.parseDouble(inputBrightness1.getText());
            beta = Double.parseDouble(inputBrightness2.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid input", "The values must be numbers");
        }


        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = enchantmentService.increaseBrightness(processedImage, alpha, beta);
        stateService.setProcessedImage(transformedImage);

        Mat histogramMat = EnchantmentService.calculateHistogram(processedImage);
        Image histogram = toFXImage(histogramMat);
        histogramImage.setImage(histogram);
    }

    @FXML
    public void whiteBalance() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = enchantmentService.whiteBalance(processedImage);
        stateService.setProcessedImage(transformedImage);

        Mat histogramMat = EnchantmentService.calculateHistogram(processedImage);
        Image histogram = toFXImage(histogramMat);
        histogramImage.setImage(histogram);
    }

    @FXML
    public void changeSaturation() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        double saturationAdjustment = 0; // Default value

        try {
            saturationAdjustment = Double.parseDouble(inputSaturation.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid input", "The value must be a number");
        }

        if (saturationAdjustment < -100 || saturationAdjustment > 100) {
            throw new NumberFormatException("Value out of range");
        }

        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = enchantmentService.saturation(processedImage, saturationAdjustment);
        stateService.setProcessedImage(transformedImage);

        Mat histogramMat = EnchantmentService.calculateHistogram(processedImage);
        Image histogram = toFXImage(histogramMat);
        histogramImage.setImage(histogram);
    }

    @FXML
    public void colorBalanceAdjust() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        float redGain = 1.0f;
        float greenGain = 1.0f;
        float blueGain = 1.0f;

        try {
            redGain = Float.parseFloat(inputGainRed.getText());
            greenGain = Float.parseFloat(inputGainGreen.getText());
            blueGain = Float.parseFloat(inputGainBlue.getText());
        } catch(NumberFormatException e) {
            showAlert("Invalid input", "The value must be a number");
        }

        Mat processedImage = stateService.getProcessedImage();
        System.out.println("Red Gain: " + redGain + " Green Gain: " + greenGain + " Blue Gain: " + blueGain);
        Mat transformedImage = enchantmentService.colourBalanceAdjustment(processedImage, redGain, greenGain, blueGain);
        stateService.setProcessedImage(transformedImage);

        Mat histogramMat = EnchantmentService.calculateHistogram(processedImage);
        Image histogram = toFXImage(histogramMat);
        histogramImage.setImage(histogram);
    }

    @FXML
    public void gammaCorection() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = enchantmentService.gammaCorrection(processedImage, 0.4);
        stateService.setProcessedImage(transformedImage);

        Mat histogramMat = EnchantmentService.calculateHistogram(processedImage);
        Image histogram = toFXImage(histogramMat);
        histogramImage.setImage(histogram);
    }
}

