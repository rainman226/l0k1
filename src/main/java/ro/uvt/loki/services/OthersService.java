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
        Mat destination;

        Mat gray = new Mat();
        Imgproc.cvtColor(source, gray, Imgproc.COLOR_BGR2GRAY);

        // reduce noise
        Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);

        // binary tresholding
        Mat binary = new Mat();
        Imgproc.threshold(gray, binary, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

        // noise removal with morphological operations
        Mat kernel = Mat.ones(new Size(3, 3), CvType.CV_8U);
        Mat sureBg = new Mat();
        Imgproc.dilate(binary, sureBg, kernel, new Point(-1, -1), 3);

        // find sure foreground area using distance transform
        Mat distTransform = new Mat();
        Imgproc.distanceTransform(binary, distTransform, Imgproc.DIST_L2, 5);
        Core.normalize(distTransform, distTransform, 0, 1.0, Core.NORM_MINMAX);

        Mat sureFg = new Mat();
        Imgproc.threshold(distTransform, sureFg, 0.7, 1.0, Imgproc.THRESH_BINARY);
        sureFg.convertTo(sureFg, CvType.CV_8U);

        // unknown region
        Mat unknown = new Mat();
        Core.subtract(sureBg, sureFg, unknown);

        // labelling
        Mat markers = new Mat();
        Imgproc.connectedComponents(sureFg, markers);

        // add one to all labels so that sure background is not 0, but 1
        Core.add(markers, Scalar.all(1), markers);

        // mark unknown region with 0
        for (int i = 0; i < markers.rows(); i++) {
            for (int j = 0; j < markers.cols(); j++) {
                if (unknown.get(i, j)[0] == 255) {
                    markers.put(i, j, 0);
                }
            }
        }
        destination = source.clone();

        Imgproc.watershed(destination, markers);

        // Mmark the boundaries in the image
        for (int i = 0; i < markers.rows(); i++) {
            for (int j = 0; j < markers.cols(); j++) {
                if (markers.get(i, j)[0] == -1) {
                    destination.put(i, j, 0, 0, 255); // Mark boundaries in red
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
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(kernelSize, kernelSize));

        Mat result = new Mat();

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
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(kernelSize, kernelSize));

        Mat result = new Mat();

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
        // Parameters for Harris Corner Detection
        int blockSize = 2;          // Neighborhood size
        int kSize = 3;              // Aperture parameter for Sobel operator
        double k = 0.04;            // Harris detector free parameter
        double threshold = 150;     // Threshold for detecting corners

        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        Mat grayFloat = new Mat();
        gray.convertTo(grayFloat, CvType.CV_32FC1);

        Mat harrisCorners = new Mat();
        Imgproc.cornerHarris(grayFloat, harrisCorners, blockSize, kSize, k);

        // normalize the output
        Mat harrisCornersNorm = new Mat();
        Core.normalize(harrisCorners, harrisCornersNorm, 0, 255, Core.NORM_MINMAX);

        // convert to 8-bit type
        Mat harrisCornersNormScaled = new Mat();
        Core.convertScaleAbs(harrisCornersNorm, harrisCornersNormScaled);

        // create a copy of the source image
        Mat dst = src.clone();

        // threshold for an optimal value; it may vary depending on the image.
        for (int j = 0; j < harrisCornersNorm.rows(); j++) {
            for (int i = 0; i < harrisCornersNorm.cols(); i++) {
                if ((int) harrisCornersNorm.get(j, i)[0] > threshold) {
                    Imgproc.circle(dst, new Point(i, j), 5, new Scalar(0, 0, 255), 2);
                }
            }
        }

        return dst;
    }
}
