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
import ro.uvt.loki.controllers.EdgeDetectionController;
import ro.uvt.loki.controllers.EnchantmentController;
import ro.uvt.loki.controllers.FilterController;
import ro.uvt.loki.controllers.SegmentationController;
import ro.uvt.loki.dialogControllers.ColorBalanceController;
import ro.uvt.loki.services.*;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

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
    @FXML
    private MenuBar menuBar;

    private boolean showingOriginal = true;

    private StateService stateService = new StateService();

    private EnchantmentController enchantmentController;
    private FilterController filterController;
    private EdgeDetectionController edgeDetectionController;
    private SegmentationController segmentationController;

    @FXML
    public void initialize() {
        initializeControllers();
    }

    private void initializeControllers() {
        try {
            FXMLLoader loader;

            // Load EnchantmentMenu
            loader = new FXMLLoader(getClass().getResource("EnchantmentMenu.fxml"));
            Menu enchantmentMenu = loader.load();
            enchantmentController = loader.getController();
            enchantmentController.setStateService(stateService);
            menuBar.getMenus().add(enchantmentMenu);

            // Load FilterMenu
            loader = new FXMLLoader(getClass().getResource("FilterMenu.fxml"));
            Menu filterMenu = loader.load();
            filterController = loader.getController();
            filterController.setStateService(stateService);
            menuBar.getMenus().add(filterMenu);

            // Load EdgeDetectionMenu
            loader = new FXMLLoader(getClass().getResource("EdgeDetectionMenu.fxml"));
            Menu edgeDetectionMenu = loader.load();
            edgeDetectionController = loader.getController();
            edgeDetectionController.setStateService(stateService);
            menuBar.getMenus().add(edgeDetectionMenu);

            // Load SegmentationMenu
            loader = new FXMLLoader(getClass().getResource("SegmentationMenu.fxml"));
            Menu segmentationMenu = loader.load();
            segmentationController = loader.getController();
            segmentationController.setStateService(stateService);
            menuBar.getMenus().add(segmentationMenu);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openImage(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image selectedImage = new Image(selectedFile.toURI().toString());
            myImageView.setImage(selectedImage);
            String imagePath = selectedFile.toURI().getPath().substring(1);
            Mat originalImage = Imgcodecs.imread(imagePath);
            if (originalImage.empty()) {
                System.err.println("Cannot read image: " + imagePath);
                System.exit(0);
            }
            stateService.setOriginalImage(originalImage);
            initialize();
            myImageView.setImage(toFXImage(originalImage));
            showingOriginal = true;
        }
    }

    @FXML
    public void undo(ActionEvent event) {
        stateService.undo();
        myImageView.setImage(toFXImage(stateService.getProcessedImage()));
        showingOriginal = false;
    }

    @FXML
    private void toggleImage(ActionEvent event) {
        if (showingOriginal) {
            myImageView.setImage(toFXImage(stateService.getProcessedImage()));
        } else {
            myImageView.setImage(toFXImage(stateService.getOriginalImage()));
        }
        showingOriginal = !showingOriginal;
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
