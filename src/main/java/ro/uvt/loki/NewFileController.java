package ro.uvt.loki;

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
    private Button toggleButton;
    @FXML
    private ImageView histogramImage;

    TextInputDialog dialog;

    private Mat originalImage;

    private Mat processedImage;

    private boolean showingOriginal = true;

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

        // Initialize original and processed images
        originalImage = Imgcodecs.imread(imagePath);
        if (originalImage.empty()) {
            System.err.println("Cannot read image: " + imagePath);
            System.exit(0);
        }
        processedImage = originalImage.clone(); // Initialize processedImage as a clone of originalImage

        // Show the original image initially
        myImageView.setImage(toFXImage(originalImage));
        showingOriginal = true; // Ensure the flag is correctly set
    }

    @FXML
    private void toggleImage(ActionEvent event) {
        if (showingOriginal) {
            if (processedImage != null) {
                myImageView.setImage(toFXImage(processedImage));
            } else {
                System.err.println("Processed image is not available.");
            }
        } else {
            if (originalImage != null) {
                myImageView.setImage(toFXImage(originalImage));
            } else {
                System.err.println("Original image is not available.");
            }
        }
        showingOriginal = !showingOriginal;
    }

    public void setHistogramImage(ActionEvent event) {
        initializeImages();

        Mat histogramMat = enchantmentService.calculateHistogram(processedImage);
        processedImage = enchantmentService.equaliseHistogram(processedImage);

        if (myImageView != null) {
            Image editedImage = toFXImage(processedImage);
            myImageView.setImage(editedImage);

            Image histogram = toFXImage(histogramMat);
            histogramImage.setImage(histogram);

            showingOriginal = false;
        }
    }

    public void increaseBrightness(ActionEvent event) {
        initializeImages();

        String[] values = showInputDialog();
        double alpha = Double.parseDouble(values[0]);
        double beta = Double.parseDouble(values[1]);

        processedImage = enchantmentService.increaseBrightness(processedImage, alpha, beta);

        if (myImageView != null) {
            myImageView.setImage(toFXImage(processedImage));
            showingOriginal = false;  // Switch to processed image
        }
    }

    public void blurImage(ActionEvent event) {
        initializeImages();

        processedImage = filterService.gaussianBlur(processedImage);

        if (myImageView != null) {
            Image editedImage = toFXImage(processedImage);
            myImageView.setImage(editedImage);
            showingOriginal = false;
        }
    }

    public void whiteBalance(ActionEvent event) {
        initializeImages();

        processedImage = enchantmentService.whiteBalance(processedImage);

        if (myImageView != null) {
            Image editedImage = toFXImage(processedImage);
            myImageView.setImage(editedImage);
            showingOriginal = false;
        }
    }

    public void changeSaturation(ActionEvent event) {
        initializeImages();

        String value = saturationInputDialog();
        double saturationAdjustment = Double.parseDouble(value);
        processedImage = enchantmentService.saturation(processedImage, saturationAdjustment);

        if (myImageView != null) {
            Image editedImage = toFXImage(processedImage);
            myImageView.setImage(editedImage);
            showingOriginal = false;
        }
    }

    public void colorBalanceAdjust(ActionEvent event) {
        initializeImages();

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
                processedImage = enchantmentService.colourBalanceAdjustment(processedImage, redGain, greenGain, blueGain);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (myImageView != null) {
            Image editedImage = toFXImage(processedImage);
            myImageView.setImage(editedImage);
            showingOriginal = false;
        }
    }

    public void gammaCorection(ActionEvent event) {
        initializeImages();

        processedImage = enchantmentService.gammaCorrection(processedImage, 0.4);

        if (myImageView != null) {
            Image editedImage = toFXImage(processedImage);
            myImageView.setImage(editedImage);
            showingOriginal = false;
        }
    }

    public void sharpen(ActionEvent event) {
        initializeImages();

        String[] values = showSaturationInputDialog();
        double radius = Double.parseDouble(values[0]);
        double amount = Double.parseDouble(values[1]);

        processedImage = filterService.sharpen(processedImage, radius, amount);

        if (myImageView != null) {
            Image editedImage = toFXImage(processedImage);
            myImageView.setImage(editedImage);
            showingOriginal = false;
        }
    }

    public void sobelEdgeDetection(ActionEvent event) {
        initializeImages();

        processedImage = edgeDetectionService.sobel(processedImage, 2);

        if (myImageView != null) {
            Image editedImage = toFXImage(processedImage);
            myImageView.setImage(editedImage);
            showingOriginal = false;
        }
    }

    public void prewittEdgeDetection(ActionEvent event) {
        initializeImages();

        processedImage = edgeDetectionService.prewitt(processedImage, 10);

        if (myImageView != null) {
            Image editedImage = toFXImage(processedImage);
            myImageView.setImage(editedImage);
            showingOriginal = false;
        }
    }

    public void robertsEdgeDetection(ActionEvent event) {
        initializeImages();

        processedImage = edgeDetectionService.robertsCross(processedImage, 2);

        if (myImageView != null) {
            Image editedImage = toFXImage(processedImage);
            myImageView.setImage(editedImage);
            showingOriginal = false;
        }
    }

    public void differenceOfGaussians(ActionEvent event) {
        initializeImages();

        processedImage = edgeDetectionService.differenceOfGaussians(processedImage, 2, 4, 1);

        if (myImageView != null) {
            Image editedImage = toFXImage(processedImage);
            myImageView.setImage(editedImage);
            showingOriginal = false;
        }
    }

    public void watershedSegmentation(ActionEvent event) {
        initializeImages();

        processedImage = segmentationService.applyWatershed(processedImage);

        if (myImageView != null) {
            Image editedImage = toFXImage(processedImage);
            myImageView.setImage(editedImage);
            showingOriginal = false;
        }
    }

    public void medianFilter(ActionEvent event) {
        initializeImages();

        processedImage = filterService.medianFilter(processedImage);

        if (myImageView != null) {
            Image editedImage = toFXImage(processedImage);
            myImageView.setImage(editedImage);
            showingOriginal = false;
        }
    }


    public void dialog(ActionEvent event) {
        HelperFunctions.showInputDialog();
    }

    private void initializeImages() {
        if (originalImage == null) {
            originalImage = Imgcodecs.imread(imagePath);

            if (originalImage.empty()) {
                System.err.println("Cannot read image: " + imagePath);
                System.exit(0);
            }
        }

        if (processedImage == null) {
            processedImage = originalImage.clone();
        }
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
