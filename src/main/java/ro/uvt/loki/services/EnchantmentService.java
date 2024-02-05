package ro.uvt.loki.services;

import javafx.scene.image.Image;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import static org.bytedeco.opencv.global.opencv_core.addWeighted;
import static org.bytedeco.opencv.global.opencv_imgproc.GaussianBlur;
import static ro.uvt.loki.HelperFunctions.imshow;


public class EnchantmentService {
    public Mat increaseBrightness(Mat source) {
        source.convertTo(source, -1, 5, 25);
        //imshow("Test", source);
        return source;
    }

    public Mat sharpen(Mat source) {
        Mat destination
                = new Mat(source.rows(), source.cols(),
                source.type());
        GaussianBlur(source, destination, new Size(0, 0), 10);
        addWeighted(source, 1.5, destination, -0.5, 0, destination);
        //imshow("test", destination);
        return destination;
    }
}
