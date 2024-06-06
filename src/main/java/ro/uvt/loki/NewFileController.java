package ro.uvt.loki;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import ro.uvt.loki.controllers.*;
import ro.uvt.loki.services.EnchantmentService;
import ro.uvt.loki.services.StateService;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static ro.uvt.loki.HelperFunctions.toFXImage;


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
    private AnchorPane categoryContainer;
    @FXML
    private ImageView histogramImage;
    @FXML
    private MenuBar menuBar;

    private boolean showingOriginal = true;

    private StateService stateService = StateService.getInstance();

    @FXML
    public void initialize() {
        stateService.processedImageProperty().addListener(new ChangeListener<Mat>() {
            @Override
            public void changed(ObservableValue<? extends Mat> observable, Mat oldValue, Mat newValue) {
                myImageView.setImage(toFXImage(newValue));
                showingOriginal = false;
            }
        });
        initializeControllers();
    }

    private void initializeControllers() {
        try {
            // Load EnchantmentMenu
            FXMLLoader enchantmentLoader = new FXMLLoader(getClass().getResource("EnchantmentMenu.fxml"));
            Menu enchantmentMenu = enchantmentLoader.load();
            EnchantmentController enchantmentController = enchantmentLoader.getController();
            enchantmentController.setHistogramImage(histogramImage);
            //menuBar.getMenus().add(enchantmentMenu);

            // Load FilterMenu
            FXMLLoader filterLoader = new FXMLLoader(getClass().getResource("FilterMenu.fxml"));
            Menu filterMenu = filterLoader.load();
            FilterController filterController = filterLoader.getController();
            //menuBar.getMenus().add(filterMenu);

            // Load EdgeDetectionMenu
            FXMLLoader edgeDetectionLoader = new FXMLLoader(getClass().getResource("EdgeDetectionMenu.fxml"));
            Menu edgeDetectionMenu = edgeDetectionLoader.load();
            EdgeDetectionController edgeDetectionController = edgeDetectionLoader.getController();
            //menuBar.getMenus().add(edgeDetectionMenu);

            // Load SegmentationMenu
            FXMLLoader segmentationLoader = new FXMLLoader(getClass().getResource("SegmentationMenu.fxml"));
            Menu segmentationMenu = segmentationLoader.load();
            SegmentationController segmentationController = segmentationLoader.getController();
            //menuBar.getMenus().add(segmentationMenu);

            // Load RestorationMenu
            FXMLLoader restorationLoader = new FXMLLoader(getClass().getResource("RestorationMenu.fxml"));
            Menu restorationMenu = restorationLoader.load();
            RestorationController restorationController = restorationLoader.getController();
            //menuBar.getMenus().add(restorationMenu);

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
            //initialize();
            myImageView.setImage(toFXImage(originalImage));
            showingOriginal = true;

            Mat histogramMat = EnchantmentService.calculateHistogram(stateService.getOriginalImage());
            Image histogram = toFXImage(histogramMat);
            histogramImage.setImage(histogram);
        }
    }

    @FXML
    public void saveImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpg"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp")
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            Mat processedImage = stateService.getProcessedImage();
            String filePath = file.getAbsolutePath();

            // Ensure the file extension matches the chosen filter
            if (!filePath.endsWith(".png") && !filePath.endsWith(".jpg") && !filePath.endsWith(".bmp")) {
                FileChooser.ExtensionFilter selectedExtensionFilter = fileChooser.getSelectedExtensionFilter();
                if (selectedExtensionFilter != null) {
                    String extension = selectedExtensionFilter.getExtensions().get(0).substring(1);
                    filePath += extension;
                }
            }

            boolean result = Imgcodecs.imwrite(filePath, processedImage);
            if (!result) {
                System.err.println("Failed to save image to: " + filePath);
            }
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

    @FXML
    public void switchToMain(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.setScene(new Scene(root));

            // Access the MainController if needed
            MainController mainController = loader.getController();
            // Add any logic or data passing between controllers
            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/main.css")).toExternalForm());

        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception
        }
    }

    private void loadFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane newLoadedPane = loader.load();
            categoryContainer.getChildren().clear();
            categoryContainer.getChildren().add(newLoadedPane);

            // Get the controller instance
            Object controller = loader.getController();

            // EnchentmentController needs the histogram image
            if (controller instanceof EnchantmentController) {
                EnchantmentController enchantmentController = (EnchantmentController) controller;
                
                enchantmentController.setHistogramImage(histogramImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadEnchantment() {
        loadFXML("EnchantmentView.fxml");
    }

    @FXML
    private void loadFilter() {
        loadFXML("FilterView.fxml");
    }

    @FXML
    private void loadEdgeDetection() {
        loadFXML("EdgeDetectionView.fxml");
    }

    @FXML
    private void loadRestoration() {
        loadFXML("RestorationView.fxml");
    }

    @FXML
    private void loadOther() {
        loadFXML("OthersView.fxml");
    }
}
