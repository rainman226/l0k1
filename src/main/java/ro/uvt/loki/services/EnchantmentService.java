package ro.uvt.loki.services;

import javafx.scene.image.Image;

import org.opencv.core.Mat;
import org.opencv.core.Size;

import static org.opencv.core.Core.addWeighted;
import static org.opencv.imgproc.Imgproc.GaussianBlur;



public class EnchantmentService {
    public Mat increaseBrightness(Mat source) {
        source.convertTo(source, -1, 1, 100);
        //HighGui.imshow("Test", source);

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
