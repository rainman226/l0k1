package ro.uvt.loki.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import ro.uvt.loki.services.EnchantmentService;
import ro.uvt.loki.services.StateService;

import static ro.uvt.loki.HelperFunctions.toFXImage;

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
    public void setHistogramImage(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        Mat histogramMat = EnchantmentService.calculateHistogram(processedImage);
        Mat transformedImage = enchantmentService.equaliseHistogram(processedImage);

        stateService.setProcessedImage(transformedImage);
        Image histogram = toFXImage(histogramMat);
        histogramImage.setImage(histogram);
    }

    @FXML
    public void increaseBrightness(ActionEvent event) {
        double alpha = 1.0; // Default value in case the user doesn't input anything
        double beta = 1.0;  // Same ideea

        try {
            alpha = Double.parseDouble(inputBrightness1.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for brightness1. Using default value: " + alpha);
        }

        try {
            beta = Double.parseDouble(inputBrightness2.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for brightness2. Using default value: " + beta);
        }

        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = enchantmentService.increaseBrightness(processedImage, alpha, beta);
        stateService.setProcessedImage(transformedImage);

        Mat histogramMat = EnchantmentService.calculateHistogram(processedImage);
        Image histogram = toFXImage(histogramMat);
        histogramImage.setImage(histogram);
    }

    @FXML
    public void whiteBalance(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = enchantmentService.whiteBalance(processedImage);
        stateService.setProcessedImage(transformedImage);

        Mat histogramMat = EnchantmentService.calculateHistogram(processedImage);
        Image histogram = toFXImage(histogramMat);
        histogramImage.setImage(histogram);
    }

    @FXML
    public void changeSaturation(ActionEvent event) {
        double saturationAdjustment = 0; // Default value

        try {
            saturationAdjustment = Double.parseDouble(inputSaturation.getText());
            if (saturationAdjustment < -100 || saturationAdjustment > 100) {
                throw new NumberFormatException("Value out of range");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for saturation. Using default value: " + saturationAdjustment);
        }

        Mat processedImage = stateService.getProcessedImage();
        Mat transformedImage = enchantmentService.saturation(processedImage, saturationAdjustment);
        stateService.setProcessedImage(transformedImage);

        Mat histogramMat = EnchantmentService.calculateHistogram(processedImage);
        Image histogram = toFXImage(histogramMat);
        histogramImage.setImage(histogram);
    }

    @FXML
    public void colorBalanceAdjust(ActionEvent event) {
        float redGain = 1.0f;
        float greenGain = 1.0f;
        float blueGain = 1.0f;

        try {
            redGain = Float.parseFloat(inputGainRed.getText());
        } catch(NumberFormatException e) {
            System.out.println("Invalid input for red gain. Using default value");
        }

        try {
            greenGain = Float.parseFloat(inputGainGreen.getText());
        } catch(NumberFormatException e) {
            System.out.println("Invalid input for green gain. Using default value");
        }

        try {
            blueGain = Float.parseFloat(inputGainBlue.getText());
        } catch(NumberFormatException e) {
            System.out.println("Invalid input for saturation. Using default value");
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
    public void gammaCorection(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = enchantmentService.gammaCorrection(processedImage, 0.4);
        stateService.setProcessedImage(transformedImage);

        Mat histogramMat = EnchantmentService.calculateHistogram(processedImage);
        Image histogram = toFXImage(histogramMat);
        histogramImage.setImage(histogram);
    }
}

