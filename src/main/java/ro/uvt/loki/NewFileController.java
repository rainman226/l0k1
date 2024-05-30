package ro.uvt.loki;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import ro.uvt.loki.dialogControllers.ColorBalanceController;
import ro.uvt.loki.services.EdgeDetectionService;
import ro.uvt.loki.services.EnchantmentService;
import ro.uvt.loki.services.FilterService;
import ro.uvt.loki.services.SegmentationService;

import java.io.File;
import java.io.IOException;

import static ro.uvt.loki.HelperFunctions.*;
import static ro.uvt.loki.dialogControllers.SaturationInputController.showSaturationInputDialog;
import static ro.uvt.loki.dialogControllers.SimpleInputController.saturationInputDialog;


public class NewFileController {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView myImageView;

    @FXML
    private Button uploadButton;

    @FXML
    private Button testButton;

    @FXML
    private ImageView histogramImage;

    TextInputDialog dialog;

    private final EnchantmentService enchantmentService = new EnchantmentService();

    private final FilterService filterService = new FilterService();

    private final EdgeDetectionService edgeDetectionService = new EdgeDetectionService();

    private final SegmentationService segmentationService = new SegmentationService();
    private String imagePath;
    @FXML
    public void openImage(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            // Load the selected image into the ImageView
            Image selectedImage = new Image(selectedFile.toURI().toString());
            myImageView.setImage(selectedImage);
            imagePath = selectedFile.toURI().getPath().substring(1);;
            System.out.println(imagePath);
        }
    }

    public void setHistogramImage(ActionEvent event) {
        Mat src = Imgcodecs.imread(imagePath);

        if (src.empty()) {
            System.err.println("Cannot read image: " + imagePath);
            System.exit(0);
        }

        Mat histogramMat = enchantmentService.calculateHistogram(src);
        src = enchantmentService.equaliseHistogram(src);

        if (myImageView != null) {
            Image editedImage = toFXImage(src);
            myImageView.setImage(editedImage);

            Image histogram = toFXImage(histogramMat);
            histogramImage.setImage(histogram);
        }
    }

    public void increaseBrightness(ActionEvent event) {
        Mat src = Imgcodecs.imread(imagePath);

        if (src.empty()) {
            System.err.println("Cannot read image: " + imagePath);
            System.exit(0);
        }

        String[] values = showInputDialog();
        double alpha = Double.parseDouble(values[0]);
        double beta = Double.parseDouble(values[1]);
        src = enchantmentService.increaseBrightness(src,alpha,beta);

        if (myImageView != null) {
            Image editedImage = toFXImage(src);
            myImageView.setImage(editedImage);

        }
    }

    public void blurImage(ActionEvent event) {
        Mat src = Imgcodecs.imread(imagePath);

        if (src.empty()) {
            System.err.println("Cannot read image: " + imagePath);
            System.exit(0);
        }

        src = filterService.gaussianBlur(src);

        if (myImageView != null) {
            Image editedImage = toFXImage(src);
            myImageView.setImage(editedImage);
        }
    }

    public void whiteBalance(ActionEvent event) {
        Mat src = Imgcodecs.imread(imagePath);

        if (src.empty()) {
            System.err.println("Cannot read image: " + imagePath);
            System.exit(0);
        }

        src = enchantmentService.whiteBalance(src);

        if (myImageView != null) {
            Image editedImage = toFXImage(src);
            myImageView.setImage(editedImage);
        }
    }

    public void changeSaturation(ActionEvent event) {
        Mat src = loadImage(imagePath);

        String value = saturationInputDialog();
        double saturationAdjustment = Double.parseDouble(value);
        src = enchantmentService.saturation(src, saturationAdjustment);

        if (myImageView != null) {
            Image editedImage = toFXImage(src);
            myImageView.setImage(editedImage);
        }
    }

    public void colorBalanceAdjust(ActionEvent event) {
        Mat src = Imgcodecs.imread(imagePath);

        if (src.empty()) {
            System.err.println("Cannot read image: " + imagePath);
            System.exit(0);
        }

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
                src = enchantmentService.colourBalanceAdjustment(src, redGain, greenGain, blueGain);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (myImageView != null) {
            Image editedImage = toFXImage(src);
            myImageView.setImage(editedImage);
        }
    }

    public void gammaCorection(ActionEvent event) {
        Mat src = Imgcodecs.imread(imagePath);

        if (src.empty()) {
            System.err.println("Cannot read image: " + imagePath);
            System.exit(0);
        }

        src = enchantmentService.gammaCorrection(src, 0.4);

        if (myImageView != null) {
            Image editedImage = toFXImage(src);
            myImageView.setImage(editedImage);
        }
    }

    public void sharpen(ActionEvent event) {
        Mat src = Imgcodecs.imread(imagePath);

        if (src.empty()) {
            System.err.println("Cannot read image: " + imagePath);
            System.exit(0);
        }

        String[] values = showSaturationInputDialog();
        double radius = Double.parseDouble(values[0]);
        double amount = Double.parseDouble(values[1]);

        src = filterService.sharpen(src, radius, amount);

        if (myImageView != null) {
            Image editedImage = toFXImage(src);
            myImageView.setImage(editedImage);
        }
    }

    public void sobelEdgeDetection(ActionEvent event) {
        Mat src = Imgcodecs.imread(imagePath);

        if (src.empty()) {
            System.err.println("Cannot read image: " + imagePath);
            System.exit(0);
        }

        src = edgeDetectionService.sobel(src, 2);

        if (myImageView != null) {
            Image editedImage = toFXImage(src);
            myImageView.setImage(editedImage);
        }
    }

    public void prewittEdgeDetection(ActionEvent event) {
        Mat src = Imgcodecs.imread(imagePath);

        if (src.empty()) {
            System.err.println("Cannot read image: " + imagePath);
            System.exit(0);
        }

        src = edgeDetectionService.prewitt(src, 10);

        if (myImageView != null) {
            Image editedImage = toFXImage(src);
            myImageView.setImage(editedImage);
        }
    }

    public void robertsEdgeDetection(ActionEvent event) {
        Mat src = Imgcodecs.imread(imagePath);

        if (src.empty()) {
            System.err.println("Cannot read image: " + imagePath);
            System.exit(0);
        }

        src = edgeDetectionService.robertsCross(src, 2);

        if (myImageView != null) {
            Image editedImage = toFXImage(src);
            myImageView.setImage(editedImage);
        }
    }

    public void differenceOfGaussians(ActionEvent event) {
        Mat src = Imgcodecs.imread(imagePath);

        if (src.empty()) {
            System.err.println("Cannot read image: " + imagePath);
            System.exit(0);
        }

        src = edgeDetectionService.differenceOfGaussians(src, 2, 4, 1);

        if (myImageView != null) {
            Image editedImage = toFXImage(src);
            myImageView.setImage(editedImage);
        }
    }

    public void watershedSegmentation(ActionEvent event) {
        Mat src = Imgcodecs.imread(imagePath);

        if (src.empty()) {
            System.err.println("Cannot read image: " + imagePath);
            System.exit(0);
        }

        src = segmentationService.applyWatershed(src);

        if (myImageView != null) {
            Image editedImage = toFXImage(src);
            myImageView.setImage(editedImage);
        }
    }

    public void medianFilter(ActionEvent event) {
        Mat src = Imgcodecs.imread(imagePath);

        if (src.empty()) {
            System.err.println("Cannot read image: " + imagePath);
            System.exit(0);
        }

        src = filterService.medianFilter(src);

        if (myImageView != null) {
            Image editedImage = toFXImage(src);
            myImageView.setImage(editedImage);
        }
    }


    public void dialog(ActionEvent event) {
        HelperFunctions.showInputDialog();
    }

    public void switchToMain(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.setScene(new Scene(root));

            // Access the MainController if needed
            MainController mainController = loader.getController();
            // Add any logic or data passing between controllers

        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception
        }
    }

}
