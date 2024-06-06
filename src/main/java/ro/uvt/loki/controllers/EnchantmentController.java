package ro.uvt.loki.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import ro.uvt.loki.dialogControllers.ColorBalanceController;
import ro.uvt.loki.services.EnchantmentService;
import ro.uvt.loki.services.StateService;

import java.io.IOException;

import static ro.uvt.loki.HelperFunctions.showInputDialog;
import static ro.uvt.loki.HelperFunctions.toFXImage;
import static ro.uvt.loki.dialogControllers.SimpleInputController.saturationInputDialog;

public class EnchantmentController {
    private final StateService stateService = StateService.getInstance();
    private final EnchantmentService enchantmentService = new EnchantmentService();


    private ImageView histogramImage;

    public void setHistogramImage(ImageView histogramImage) {
        this.histogramImage = histogramImage;
        System.out.println("Am legat imaginile sefule");
    }

    @FXML
    public void setHistogramImage(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        Mat histogramMat = enchantmentService.calculateHistogram(processedImage);
        Mat transformedImage = enchantmentService.equaliseHistogram(processedImage);

        stateService.setProcessedImage(transformedImage);
        Image histogram = toFXImage(histogramMat);
        histogramImage.setImage(histogram);
    }

    @FXML
    public void increaseBrightness(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        String[] values = showInputDialog();
        double alpha = Double.parseDouble(values[0]);
        double beta = Double.parseDouble(values[1]);

        Mat transformedImage = enchantmentService.increaseBrightness(processedImage, alpha, beta);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void whiteBalance(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = enchantmentService.whiteBalance(processedImage);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void changeSaturation(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        String value = saturationInputDialog();
        double saturationAdjustment = Double.parseDouble(value);
        Mat transformedImage = enchantmentService.saturation(processedImage, saturationAdjustment);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void colorBalanceAdjust(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ColorBalanceEditor.fxml"));
            DialogPane dialogPane = loader.load();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Color Balance Adjustment");

            dialog.getDialogPane().setContent(dialogPane);

            ColorBalanceController colorBalanceController = loader.getController();
            colorBalanceController.setDialogStage((Stage) dialog.getDialogPane().getScene().getWindow());

            dialog.showAndWait();  // This will wait for the dialog to close

            // Once the dialog is closed, you can directly access the values from the controller
            if (colorBalanceController.isOkClicked()) {
                float redGain = colorBalanceController.getRedGain();
                float greenGain = colorBalanceController.getGreenGain();
                float blueGain = colorBalanceController.getBlueGain();

                System.out.println("Red Gain: " + redGain + " Green Gain: " + greenGain + " Blue Gain: " + blueGain);
                Mat transformedImage = enchantmentService.colourBalanceAdjustment(processedImage, redGain, greenGain, blueGain);
                stateService.setProcessedImage(transformedImage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void gammaCorection(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        Mat transformedImage = enchantmentService.gammaCorrection(processedImage, 0.4);
        stateService.setProcessedImage(transformedImage);
    }
}

