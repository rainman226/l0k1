package ro.uvt.loki;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import org.bytedeco.opencv.opencv_core.*;
import ro.uvt.loki.services.EnchantmentService;

import java.io.File;
import java.io.IOException;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static ro.uvt.loki.HelperFunctions.toFXImage;


public class NewFileController {
    @FXML
    private ImageView myImageView;

    @FXML
    private Button uploadButton;

    @FXML
    private Button testButton;

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

    public void brightness(ActionEvent event) {
        Mat src = imread(imagePath);

        if (src.empty())
            return;

        src = enchantmentService.increaseBrightness(src);
        if (myImageView != null) {
            Image editedImage = toFXImage(src);
            myImageView.setImage(editedImage);
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
