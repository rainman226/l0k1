package ro.uvt.loki;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.JavaFXFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

public class HelperFunctions {
    public static javafx.scene.image.Image toFXImage(Mat mat) {
        try(OpenCVFrameConverter.ToMat openCVConverter = new OpenCVFrameConverter.ToMat()) {
            try(Frame frame = openCVConverter.convert(mat)){
                try(JavaFXFrameConverter javaFXConverter  = new JavaFXFrameConverter()) {
                    return javaFXConverter.convert(frame);
                }
            }
        }
    }

    public static void imshow(String txt, Mat img) {
        CanvasFrame canvasFrame = new CanvasFrame(txt);
        canvasFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        canvasFrame.setCanvasSize(img.cols(), img.rows());
        canvasFrame.showImage(new OpenCVFrameConverter.ToMat().convert(img));
    }
}
