package ro.uvt.loki.services;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class EdgeDetectionService {
    public Mat sobel(Mat source, double amount) {
        // Define fixed parameters
        int kernelSize = 3;
        double scale = 1.0;
        double delta = 0.0;
        int borderType = Core.BORDER_DEFAULT;
        boolean applyBlur = true;
        Size blurKernelSize = new Size(3, 3);
        double blurSigmaX = 0;

        // Convert the source image to grayscale if it is not already
        Mat gray = new Mat();
        if (source.channels() > 1) {
            Imgproc.cvtColor(source, gray, Imgproc.COLOR_BGR2GRAY);
        } else {
            gray = source.clone();
        }

        // Apply Gaussian blur to reduce noise and improve edge detection
        Mat blurred = new Mat();
        if (applyBlur) {
            Imgproc.GaussianBlur(gray, blurred, blurKernelSize, blurSigmaX);
        } else {
            blurred = gray.clone();
        }

        // Calculate gradients in the x and y directions
        Mat gradX = new Mat();
        Mat gradY = new Mat();
        Imgproc.Sobel(blurred, gradX, CvType.CV_64F, 1, 0, kernelSize, scale, delta, borderType);
        Imgproc.Sobel(blurred, gradY, CvType.CV_64F, 0, 1, kernelSize, scale, delta, borderType);

        // Convert gradients to absolute values
        Core.convertScaleAbs(gradX, gradX);
        Core.convertScaleAbs(gradY, gradY);

        // Combine gradients to get the overall edge strength
        Mat edges = new Mat();
        Core.addWeighted(gradX, 0.5, gradY, 0.5, 0, edges);

        // Apply the "amount" parameter to scale the edge intensities
        Core.multiply(edges, new Mat(edges.size(), edges.type(), new Scalar(amount)), edges);

        return edges;
    }

    public Mat prewitt(Mat src, double amount) {
        // Define fixed parameters
        int kernelSize = 3;
        double scale = 1.0;
        double delta = 0.0;
        int borderType = Core.BORDER_DEFAULT;
        boolean applyBlur = true;
        Size blurKernelSize = new Size(3, 3);
        double blurSigmaX = 0;

        // Convert the source image to grayscale if it is not already
        Mat gray = new Mat();
        if (src.channels() > 1) {
            Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        } else {
            gray = src.clone();
        }

        // Apply Gaussian blur to reduce noise and improve edge detection
        Mat blurred = new Mat();
        if (applyBlur) {
            Imgproc.GaussianBlur(gray, blurred, blurKernelSize, blurSigmaX);
        } else {
            blurred = gray.clone();
        }

        // Define Prewitt kernels
        Mat kernelX = new Mat(3, 3, CvType.CV_32F) {
            {
                put(0, 0, -1); put(0, 1, 0); put(0, 2, 1);
                put(1, 0, -1); put(1, 1, 0); put(1, 2, 1);
                put(2, 0, -1); put(2, 1, 0); put(2, 2, 1);
            }
        };

        Mat kernelY = new Mat(3, 3, CvType.CV_32F) {
            {
                put(0, 0, -1); put(0, 1, -1); put(0, 2, -1);
                put(1, 0, 0);  put(1, 1, 0);  put(1, 2, 0);
                put(2, 0, 1);  put(2, 1, 1);  put(2, 2, 1);
            }
        };

        // Calculate gradients in the x and y directions using Prewitt kernels
        Mat gradX = new Mat();
        Mat gradY = new Mat();
        Imgproc.filter2D(blurred, gradX, CvType.CV_64F, kernelX);
        Imgproc.filter2D(blurred, gradY, CvType.CV_64F, kernelY);

        // Convert gradients to absolute values
        Core.convertScaleAbs(gradX, gradX);
        Core.convertScaleAbs(gradY, gradY);

        // Combine gradients to get the overall edge strength
        Mat edges = new Mat();
        Core.addWeighted(gradX, 0.5, gradY, 0.5, 0, edges);

        // Apply the "amount" parameter to scale the edge intensities
        Core.multiply(edges, new Mat(edges.size(), edges.type(), new Scalar(amount)), edges);

        return edges;
    }
}
