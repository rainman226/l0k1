package ro.uvt.loki.services;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class FilterService {

    public Mat gaussianBlur(Mat source) {
        Mat destination
                = new Mat(source.rows(), source.cols(),
                source.type());
        Imgproc.GaussianBlur(source, destination, new org.opencv.core.Size(15, 15), 0);
        return destination;
    }
}
