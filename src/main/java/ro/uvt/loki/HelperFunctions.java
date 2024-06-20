package ro.uvt.loki;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;

import java.io.ByteArrayInputStream;

import static org.opencv.imgcodecs.Imgcodecs.imencode;

public class HelperFunctions {

    public static javafx.scene.image.Image toFXImage(Mat mat) {
        MatOfByte byteMat = new MatOfByte();
        imencode(".bmp", mat, byteMat);
        return new Image(new ByteArrayInputStream(byteMat.toArray()));
    }

    public static void imshow(String txt, Mat img) {
        CanvasFrame canvasFrame = new CanvasFrame(txt);
        canvasFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        canvasFrame.setCanvasSize(img.cols(), img.rows());
        canvasFrame.showImage(new OpenCVFrameConverter.ToMat().convert(img));
    }

    public static void noImageSelectedAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Image Load Error");
        alert.setHeaderText(null);
        alert.setContentText("Please select an image first.");

        alert.showAndWait();
    }

    public static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }

}
