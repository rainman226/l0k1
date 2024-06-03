package ro.uvt.loki.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.opencv.core.Mat;
import ro.uvt.loki.services.FilterService;
import ro.uvt.loki.services.StateService;

import static ro.uvt.loki.dialogControllers.SaturationInputController.showSaturationInputDialog;

public class FilterController {
    private final StateService stateService = StateService.getInstance();
    private final FilterService filterService = new FilterService();

    @FXML
    public void blurImage(ActionEvent event) {
        if (stateService == null) {
            System.out.println("stateService is null");
        } else {
            Mat processedImage = stateService.getProcessedImage();

            Mat transformedImage = filterService.gaussianBlur(processedImage);
            stateService.setProcessedImage(transformedImage);
        }
    }

    @FXML
    public void medianFilter(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();
        //TODO: add input for kernel size
        Mat transformedImage = filterService.medianFilter(processedImage);
        stateService.setProcessedImage(transformedImage);
    }

    @FXML
    public void sharpen(ActionEvent event) {
        Mat processedImage = stateService.getProcessedImage();

        String[] values = showSaturationInputDialog();
        double radius = Double.parseDouble(values[0]);
        double amount = Double.parseDouble(values[1]);

        Mat transformedImage = filterService.sharpen(processedImage, radius, amount);
        stateService.setProcessedImage(transformedImage);
    }
}
