package ro.uvt.loki;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.uvt.loki.controllers.algorithms.EnchantmentController;
import ro.uvt.loki.services.EnchantmentService;
import ro.uvt.loki.services.StateService;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static ro.uvt.loki.HelperFunctions.noImageSelectedAlert;
import static ro.uvt.loki.HelperFunctions.toFXImage;


public class NewFileController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ImageView myImageView;
    @FXML
    private AnchorPane categoryContainer;
    @FXML
    private ImageView histogramImage;
    @FXML
    private Menu enchantmentMenu;

    @FXML
    private Menu filterMenu;

    @FXML
    private Menu edgeDetectionMenu;

    @FXML
    private Menu restorationMenu;

    @FXML
    private Menu otherMenu;

    @FXML
    private Menu helpMenu;

    @FXML
    private Button toggleButton;

    private boolean showingOriginal = true;

    private final StateService stateService = StateService.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(NewFileController.class);

    @FXML
    public void initialize() {
        stateService.processedImageProperty().addListener((observable, oldValue, newValue) -> {
            myImageView.setImage(toFXImage(newValue));
            showingOriginal = false;
        });

        enchantmentMenu.setOnShowing(event -> handleMenuAction(enchantmentMenu, this::loadEnchantment));
        filterMenu.setOnShowing(event -> handleMenuAction(filterMenu, this::loadFilter));
        edgeDetectionMenu.setOnShowing(event -> handleMenuAction(edgeDetectionMenu, this::loadEdgeDetection));
        restorationMenu.setOnShowing(event -> handleMenuAction(restorationMenu, this::loadRestoration));
        otherMenu.setOnShowing(event -> handleMenuAction(otherMenu, this::loadOther));
        helpMenu.setOnShowing(event -> openHelp());
    }

    // to make the menu close after clicking an item
    private void handleMenuAction(Menu menu, Runnable action) {
        action.run();
        PauseTransition pause = new PauseTransition(Duration.millis(20));
        pause.setOnFinished(e -> menu.hide());
        pause.play();
    }

    @FXML
    public void openImage() {
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
            myImageView.setImage(toFXImage(originalImage));
            showingOriginal = true;

            Mat histogramMat = EnchantmentService.calculateHistogram(stateService.getOriginalImage());
            Image histogram = toFXImage(histogramMat);
            histogramImage.setImage(histogram);
        }
    }

    @FXML
    public void saveImage() {
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
    public void undo() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        stateService.undo();
        myImageView.setImage(toFXImage(stateService.getProcessedImage()));

        showingOriginal = false;
    }

    @FXML
    private void toggleImage() {
        if(!stateService.isImageLoaded()) {
            noImageSelectedAlert();
            return;
        }

        if (showingOriginal) {
            myImageView.setImage(toFXImage(stateService.getProcessedImage()));
            toggleButton.setText("Show Original");
        } else {
            myImageView.setImage(toFXImage(stateService.getOriginalImage()));
            toggleButton.setText("Show Modified");
        }
        showingOriginal = !showingOriginal;
    }

    public void openHelp() {
        Stage helpStage = new Stage();

        helpStage.initModality(Modality.APPLICATION_MODAL);
        helpStage.initStyle(StageStyle.UTILITY);
        helpStage.setTitle("Help");

        ImageView imageView = new ImageView();
        Image helpImage = new Image("file:///D:/Projects/l0k1/loki/src/main/resources/images/helpmenu.png");
        imageView.setImage(helpImage);

        imageView.setPreserveRatio(true);
        imageView.setFitWidth(1280);
        imageView.setFitHeight(720);

        StackPane rootPane = new StackPane();
        rootPane.getChildren().add(imageView);

        Scene scene = new Scene(rootPane, 1280, 720);
        helpStage.setScene(scene);

        helpStage.showAndWait();
    }

    @FXML
    public void switchToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.setScene(new Scene(root));

            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/main.css")).toExternalForm());

        } catch (IOException e) {
            logger.error("An error occurred", e);
        }
    }

    private void loadFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane newLoadedPane = loader.load();
            categoryContainer.getChildren().clear();
            categoryContainer.getChildren().add(newLoadedPane);


            Object controller = loader.getController();

            // Need the histogram image in the EnchantmentController
            if (controller instanceof EnchantmentController enchantmentController) {

                enchantmentController.setHistogramImage(histogramImage);
            }
        } catch (Exception e) {
            logger.error("An error occurred", e);
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
