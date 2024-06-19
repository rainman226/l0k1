package ro.uvt.loki.services;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
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
