package ro.uvt.loki.services;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class FilterService {
    /**
     * Apply Gaussian blur to the source image
     * @param source the source image
     * @param size the size of the kernel
     * @return the blurred image
     */
    public Mat gaussianBlur(Mat source, double size) {
        Mat destination
                = new Mat(source.rows(), source.cols(),
                source.type());
        Imgproc.GaussianBlur(source, destination, new org.opencv.core.Size(size, size), 0);
        return destination;
    }

    /**
     * Apply sharpening to the source image
     * @param source the source image
     * @param radius the radius of the Gaussian blur
     * @param amount the amount of sharpening
     * @return the sharpened image
     */
    public Mat sharpen(Mat source, double radius, double amount) {
        Mat blurred = new Mat();
        Mat dst = new Mat();

        // Apply Gaussian blur to the source image
        Imgproc.GaussianBlur(source, blurred, new Size(0, 0), radius);

        // Use addWeighted to blend the original and the blurred image
        Core.addWeighted(source, 1.0 + amount , blurred, -amount, 0, dst);

        return dst;
    }

    /**
     * Apply median filter to the source image
     * @param source the source image
     * @param ksize the kernel size
     * @return the filtered image
     */
    public Mat medianFilter(Mat source, int ksize) {
        Mat destination
                = new Mat(source.rows(), source.cols(),
                source.type());

        //try 15
        Imgproc.medianBlur(source, destination, ksize);
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

    public Mat applyBilateralFilter(Mat src) {
        // Parameters for Bilateral Filtering
        int d = 15;  // Diameter of each pixel neighborhood
        double sigmaColor = 75;  // Filter sigma in the color space
        double sigmaSpace = 75;  // Filter sigma in the coordinate space
        Mat dst = new Mat();
        Imgproc.bilateralFilter(src, dst, d, sigmaColor, sigmaSpace, Core.BORDER_DEFAULT);
        return dst;
    }
}
