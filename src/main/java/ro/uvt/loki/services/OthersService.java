package ro.uvt.loki.services;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class OthersService {
    /**
     * Apply the watershed algorithm to segment the image
     *
     * @param source The source image
     * @return The segmented image
     */
    public Mat applyWatershed(Mat source) {
        Mat destination
                = new Mat(source.rows(), source.cols(),
                source.type());
        // Convert the image to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(source, gray, Imgproc.COLOR_BGR2GRAY);

        // Apply Gaussian blur to reduce noise
        Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);

        // Apply binary thresholding
        Mat binary = new Mat();
        Imgproc.threshold(gray, binary, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

        // Noise removal with morphological operations
        Mat kernel = Mat.ones(new Size(3, 3), CvType.CV_8U);
        Mat sureBg = new Mat();
        Imgproc.dilate(binary, sureBg, kernel, new Point(-1, -1), 3);

        // Finding sure foreground area using distance transform
        Mat distTransform = new Mat();
        Imgproc.distanceTransform(binary, distTransform, Imgproc.DIST_L2, 5);
        Core.normalize(distTransform, distTransform, 0, 1.0, Core.NORM_MINMAX);

        Mat sureFg = new Mat();
        Imgproc.threshold(distTransform, sureFg, 0.7, 1.0, Imgproc.THRESH_BINARY);
        sureFg.convertTo(sureFg, CvType.CV_8U);

        // Finding unknown region
        Mat unknown = new Mat();
        Core.subtract(sureBg, sureFg, unknown);

        // Marker labelling
        Mat markers = new Mat();
        Imgproc.connectedComponents(sureFg, markers);

        // Add one to all labels so that sure background is not 0, but 1
        Core.add(markers, Scalar.all(1), markers);

        // Now, mark the region of unknown with zero
        for (int i = 0; i < markers.rows(); i++) {
            for (int j = 0; j < markers.cols(); j++) {
                if (unknown.get(i, j)[0] == 255) {
                    markers.put(i, j, 0);
                }
            }
        }
        destination = source.clone();
        // Apply the watershed algorithm
        Imgproc.watershed(destination, markers);

        // Mark the boundaries
        for (int i = 0; i < markers.rows(); i++) {
            for (int j = 0; j < markers.cols(); j++) {
                if (markers.get(i, j)[0] == -1) {
                    destination.put(i, j, new double[]{0, 0, 255}); // Mark boundaries in red
                }
            }
        }

        return destination;
    }

    /**
     * Convert the RGB image to HSV
     *
     * @param source The source image
     * @return The HSV image
     */
    public Mat convertRGBtoHSV(Mat source) {
        Mat hsvImage = new Mat(source.size(), CvType.CV_8UC3);

        // Convert the RGB image to HSV
        Imgproc.cvtColor(source, hsvImage, Imgproc.COLOR_RGB2HSV);

        return hsvImage;
    }

    /**
     * Applies dilation to the source image.
     *
     * @param src The source image as a Mat object.
     * @param kernelSize The size of the dilation kernel.
     * @return A Mat object containing the dilated image.
     */
    public Mat applyDilation(Mat src, int kernelSize) {
        // Create a kernel (structuring element) for dilation
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(kernelSize, kernelSize));

        Mat result = new Mat();

        // Apply dilation
        Imgproc.dilate(src, result, kernel);

        return result;
    }

    /**
     * Applies erosion to the source image.
     *
     * @param src The source image as a Mat object.
     * @param kernelSize The size of the erosion kernel.
     * @return A Mat object containing the eroded image.
     */
    public Mat applyErosion(Mat src, int kernelSize) {
        // Create a kernel (structuring element) for erosion
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(kernelSize, kernelSize));

        Mat result = new Mat();

        // Apply erosion
        Imgproc.erode(src, result, kernel);

        return result;
    }

    /**
     * Applies the Harris Corner Detection to the source image.
     *
     * @param src The source image as a Mat object.
     * @return A Mat object containing the image with detected corners highlighted.
     */
    public Mat applyHarrisCorner(Mat src) {
        // Convert the source image to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        // Convert to float type
        Mat grayFloat = new Mat();
        gray.convertTo(grayFloat, CvType.CV_32FC1);

        // Apply the Harris corner detector
        Mat harrisCorners = new Mat();
        Imgproc.cornerHarris(grayFloat, harrisCorners, 2, 3, 0.04);

        // Normalize the result to [0, 255]
        Mat harrisCornersNorm = new Mat();
        Core.normalize(harrisCorners, harrisCornersNorm, 0, 255, Core.NORM_MINMAX);

        // Convert to 8-bit type
        Mat harrisCornersNormScaled = new Mat();
        Core.convertScaleAbs(harrisCornersNorm, harrisCornersNormScaled);

        // Create a copy of the source image to draw corners
        Mat dst = src.clone();

        // Threshold for an optimal value; it may vary depending on the image.
        double threshold = 200;
        for (int j = 0; j < harrisCornersNorm.rows(); j++) {
            for (int i = 0; i < harrisCornersNorm.cols(); i++) {
                if ((int) harrisCornersNorm.get(j, i)[0] > threshold) {
                    Imgproc.circle(dst, new org.opencv.core.Point(i, j), 5, new Scalar(0, 0, 255), 2);
                }
            }
        }

        return dst;
    }
}
