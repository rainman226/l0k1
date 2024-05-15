package ro.uvt.loki.services;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class FilterService {

    public Mat gaussianBlur(Mat source) {
        Mat destination
                = new Mat(source.rows(), source.cols(),
                source.type());
        Imgproc.GaussianBlur(source, destination, new org.opencv.core.Size(15, 15), 0);
        return destination;
    }

    public Mat sharpen(Mat source, double radius, double amount) {
        Mat blurred = new Mat();
        Mat dst = new Mat();

        // Apply Gaussian blur to the source image
        Imgproc.GaussianBlur(source, blurred, new Size(0, 0), radius);

        // Use addWeighted to blend the original and the blurred image
        Core.addWeighted(source, 1.0 + amount , blurred, -amount, 0, dst);

        return dst;
    }
}
