package ro.uvt.loki;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC3;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.filter2D;


public class NewFileController {
    @FXML
    private ImageView myImageView;

    @FXML
    private Button uploadButton;

    @FXML
    private Button testButton;

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

    @FXML
    public void imageSegmentation(ActionEvent event) {
        int[] WHITE = {255, 255, 255};
        int[] BLACK = {0, 0, 0};

        Mat src = imread(imagePath);
        // Check if everything was fine
        if (src.empty())
            return;

        // Change the background from white to black, since that will help later to extract
        // better results during the use of Distance Transform
        UByteIndexer srcIndexer = src.createIndexer();
        for (int x = 0; x < srcIndexer.rows(); x++) {
            for (int y = 0; y < srcIndexer.cols(); y++) {
                int[] values = new int[3];
                srcIndexer.get(x, y, values);
                if (Arrays.equals(values, WHITE)) {
                    srcIndexer.put(x, y, BLACK);
                }
            }
        }

        Mat kernel = Mat.ones(3, 3, CV_32F).asMat();
        FloatIndexer kernelIndexer = kernel.createIndexer();
        kernelIndexer.put(1, 1, -8); // an approximation of second derivative, a quite strong kernel

        // do the laplacian filtering as it is
        // well, we need to convert everything in something more deeper then CV_8U
        // because the kernel has some negative values,
        // and we can expect in general to have a Laplacian image with negative values
        // BUT a 8bits unsigned int (the one we are working with) can contain values from 0 to 255
        // so the possible negative number will be truncated
        Mat imgLaplacian = new Mat();
        Mat sharp = src; // copy source image to another temporary one
        filter2D(sharp, imgLaplacian, CV_32F, kernel);
        src.convertTo(sharp, CV_32F);
        Mat imgResult = subtract(sharp, imgLaplacian).asMat();
        // convert back to 8bits gray scale
        imgResult.convertTo(imgResult, CV_8UC3);
        imgLaplacian.convertTo(imgLaplacian, CV_8UC3);
        // imshow( "Laplace Filtered Image", imgLaplacian );


        // Convert Mat to JavaFX Image
        Image processedImage = mat2Image(src);

        // Set the processed image to your ImageView
        if (myImageView != null) {
            myImageView.setImage(processedImage);
        }
    }

    // Utility method to convert OpenCV Mat to JavaFX Image
    private Image mat2Image(Mat mat) {
        int width = mat.cols();
        int height = mat.rows();
        byte[] byteData = new byte[width * height * (int) mat.elemSize()];
        mat.data().get(byteData);
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        pixelWriter.setPixels(0, 0, width, height, PixelFormat.getByteRgbInstance(), byteData, 0, width * 3);
        return writableImage;
    }
}
