package ro.uvt.loki;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ro.uvt.loki.dialogControllers.ColorBalanceController;
import ro.uvt.loki.services.EnchantmentService;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


import static ro.uvt.loki.HelperFunctions.showInputDialog;
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
    private ImageView histogramImage;

    TextInputDialog dialog;

    private final EnchantmentService enchantmentService = new EnchantmentService();
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

        src = enchantmentService.blur(src);

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

            //dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //src = enchantmentService.colourBalanceAdjustment(src);

        if (myImageView != null) {
            Image editedImage = toFXImage(src);
            myImageView.setImage(editedImage);
        }
    }

//    public void contrastStretch(ActionEvent event) {
//        Mat src = Imgcodecs.imread(imagePath);
//
//        if (src.empty()) {
//            System.err.println("Cannot read image: " + imagePath);
//            System.exit(0);
//        }
//
//        src = enchantmentService.contrastStretch(src);
//
//        if (myImageView != null) {
//            Image editedImage = toFXImage(src);
//            myImageView.setImage(editedImage);
//        }
//    }
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
//    @FXML
//    public void imageSegmentation(ActionEvent event) {
//        int[] WHITE = {255, 255, 255};
//        int[] BLACK = {0, 0, 0};
//
//        Mat src = imread(imagePath);
//        // Check if everything was fine
//        if (src.empty())
//            return;
//
//        // Change the background from white to black, since that will help later to extract
//        // better results during the use of Distance Transform
//        UByteIndexer srcIndexer = src.createIndexer();
//        for (int x = 0; x < srcIndexer.rows(); x++) {
//            for (int y = 0; y < srcIndexer.cols(); y++) {
//                int[] values = new int[3];
//                srcIndexer.get(x, y, values);
//                if (Arrays.equals(values, WHITE)) {
//                    srcIndexer.put(x, y, BLACK);
//                }
//            }
//        }
//
//        Mat kernel = Mat.ones(3, 3, CV_32F).asMat();
//        FloatIndexer kernelIndexer = kernel.createIndexer();
//        kernelIndexer.put(1, 1, -8); // an approximation of second derivative, a quite strong kernel
//
//        // Convert Mat to JavaFX Image
//        //Image processedImage = mat2Image(src);
//
//        // Set the processed image to your ImageView
//        if (myImageView != null) {
//            //myImageView.setImage(processedImage);
//            // Encode the JavaCV Mat to a byte array
//            BytePointer bytePointer = new BytePointer();
//            imencode(".png", src, bytePointer);
//            byte[] byteArray = new byte[(int) bytePointer.capacity()];
//            bytePointer.get(byteArray);
//
//            // Convert the byte array to a JavaFX Image
//            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray)) {
//                Image processedImage = new Image(inputStream);
//
//                // Set the processed image to myImageView
//                myImageView.setImage(processedImage);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    // Utility method to convert OpenCV Mat to JavaFX Image
//    private Image mat2Image(Mat mat) {
//        int width = mat.cols();
//        int height = mat.rows();
//        byte[] byteData = new byte[width * height * (int) mat.elemSize()];
//        mat.data().get(byteData);
//        WritableImage writableImage = new WritableImage(width, height);
//        PixelWriter pixelWriter = writableImage.getPixelWriter();
//        pixelWriter.setPixels(0, 0, width, height, PixelFormat.getByteRgbInstance(), byteData, 0, width * 3);
//        return writableImage;
//    }
}
