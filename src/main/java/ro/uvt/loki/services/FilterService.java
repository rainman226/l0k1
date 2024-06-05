package ro.uvt.loki.services;

import org.opencv.core.*;
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

    public Mat medianFilter(Mat source) {
        Mat destination
                = new Mat(source.rows(), source.cols(),
                source.type());
        Imgproc.medianBlur(source, destination, 15);
        return destination;
    }

    // NOT WORKING
    public Mat anisotropicDiffusion(Mat src, int iterations, double kappa, double lambda) {
        Mat dest = src.clone();
        for (int t = 0; t < iterations; t++) {
            for (int i = 1; i < src.rows() - 1; i++) {
                for (int j = 1; j < src.cols() - 1; j++) {
                    double I = src.get(i, j)[0];
                    double I_n = src.get(i-1, j)[0];
                    double I_s = src.get(i+1, j)[0];
                    double I_e = src.get(i, j+1)[0];
                    double I_w = src.get(i, j-1)[0];

                    double c_n = Math.exp(-Math.pow((I - I_n) / kappa, 2));
                    double c_s = Math.exp(-Math.pow((I - I_s) / kappa, 2));
                    double c_e = Math.exp(-Math.pow((I - I_e) / kappa, 2));
                    double c_w = Math.exp(-Math.pow((I - I_w) / kappa, 2));

                    double update = lambda * (c_n * (I_n - I) + c_s * (I_s - I) + c_e * (I_e - I) + c_w * (I_w - I));
                    dest.put(i, j, new double[]{I + update});  // Ensure the array matches channel count
                }
            }
            src = dest.clone();
        }
        return dest;
    }
}
