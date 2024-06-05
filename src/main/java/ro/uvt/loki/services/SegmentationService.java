package ro.uvt.loki.services;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class SegmentationService {
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
}
