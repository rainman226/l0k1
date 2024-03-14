package ro.uvt.loki;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.JavaFXFrameConverter;
import org.opencv.highgui.HighGui;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


import java.io.ByteArrayInputStream;
import java.util.Optional;

import static org.opencv.imgcodecs.Imgcodecs.imencode;
//import org.bytedeco.opencv.opencv_core.Mat;

public class HelperFunctions {
//    public static javafx.scene.image.Image toFXImage(Mat mat) {
//        try(OpenCVFrameConverter.ToMat openCVConverter = new OpenCVFrameConverter.ToMat()) {
//            try(Frame frame = openCVConverter.convert(mat)){
//                try(JavaFXFrameConverter javaFXConverter  = new JavaFXFrameConverter()) {
//                    return javaFXConverter.convert(frame);
//                }
//            }
//        }
//    }

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

    public static String[] showInputDialog() {
        // Create a grid pane for multiple inputs
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Create text fields for user input
        TextField param1Field = new TextField();
        TextField param2Field = new TextField();

        // Add labels and fields to the grid
        grid.add(new Label("Alpha:"), 0, 0);
        grid.add(param1Field, 1, 0);
        grid.add(new Label("Beta:"), 0, 1);
        grid.add(param2Field, 1, 1);

        // Create the dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Enter alpha and beta values:");
        dialog.getDialogPane().setContent(grid);

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            // Get the parameters from the text fields
            String param1 = param1Field.getText();
            String param2 = param2Field.getText();

            return new String[]{param1, param2};

        }
        else return null;
    }


}
