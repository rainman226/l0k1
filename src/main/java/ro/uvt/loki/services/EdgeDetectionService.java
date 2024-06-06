package ro.uvt.loki.services;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class EdgeDetectionService {
    /**
     * Applies the Sobel edge detection algorithm to the input image.
     *
     * @param source The input image to process.
     * @param amount The scaling factor to apply to the edge intensities.
     * @return The processed image with the edges highlighted.
     */
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

    /**
     * Applies the Prewitt edge detection algorithm to the input image.
     *
     * @param src The input image to process.
     * @param amount The scaling factor to apply to the edge intensities.
     * @return The processed image with the edges highlighted.
     */
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

    /**
     * Applies the Roberts Cross edge detection algorithm to the input image.
     *
     * @param src The input image to process.
     * @param amount The scaling factor to apply to the edge intensities.
     * @return The processed image with the edges highlighted.
     */
    public Mat robertsCross(Mat src, double amount) {
        // Define fixed parameters
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

        // Define Roberts Cross kernels
        Mat kernelX = new Mat(2, 2, CvType.CV_32F) {
            {
                put(0, 0, 1); put(0, 1, 0);
                put(1, 0, 0); put(1, 1, -1);
            }
        };

        Mat kernelY = new Mat(2, 2, CvType.CV_32F) {
            {
                put(0, 0, 0); put(0, 1, 1);
                put(1, 0, -1); put(1, 1, 0);
            }
        };

        // Calculate gradients in the x and y directions using Roberts Cross kernels
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

    /**
     * Applies the Difference of Gaussians edge detection algorithm to the input image.
     *
     * @param src The input image to process.
     * @param radius1 The radius of the first Gaussian blur.
     * @param radius2 The radius of the second Gaussian blur.
     * @return The processed image with the edges highlighted.
     */
    public Mat differenceOfGaussians(Mat src, double radius1, double radius2) {
        // Convert the source image to grayscale if it is not already
        Mat gray = new Mat();
        if (src.channels() > 1) {
            Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        } else {
            gray = src.clone();
        }

        // Apply Gaussian blur with the first radius
        Mat blurred1 = new Mat();
        Imgproc.GaussianBlur(gray, blurred1, new Size(0, 0), radius1);

        // Apply Gaussian blur with the second radius
        Mat blurred2 = new Mat();
        Imgproc.GaussianBlur(gray, blurred2, new Size(0, 0), radius2);

        // Subtract the two blurred images to get the DoG result
        Mat dog = new Mat();
        Core.subtract(blurred1, blurred2, dog);

        // Normalize the result to enhance visibility
        Core.normalize(dog, dog, 0, 255, Core.NORM_MINMAX);

        // Convert to absolute values to emphasize edges
        Core.convertScaleAbs(dog, dog);

//        // Apply the "amount" parameter to scale the edge intensities
//        Core.multiply(dog, new Mat(dog.size(), dog.type(), new Scalar(amount)), dog);

        return dog;
    }
}
