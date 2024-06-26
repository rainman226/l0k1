package ro.uvt.loki.services;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_32F;


public class EnchantmentService {
    /**
     * Increase the brightness and/or contrast of the image
     * @param src the source image
     * @param alpha the brightness factor
     * @param beta the contrast factor
     * @return the image with increased contrast
     */
    public Mat increaseBrightness(Mat src, double alpha, double beta) {
        Mat destination
                = new Mat(src.rows(), src.cols(),
                src.type());
        // first value is the brightness, second is the contrast
        src.convertTo(destination, -1, alpha, beta);

        return destination;
    }

    /**
     * Calculate the histogram of the image
     * @param src the source image
     * @return the histogram of the image
     */
    public static Mat calculateHistogram(Mat src) {

        List<Mat> bgrPlanes = new ArrayList<>();
        Core.split(src, bgrPlanes);

        int histSize = 256;

        float[] range = {0, 256}; //the upper boundary is exclusive
        MatOfFloat histRange = new MatOfFloat(range);

        boolean accumulate = false;

        Mat bHist = new Mat(), gHist = new Mat(), rHist = new Mat();
        Imgproc.calcHist(bgrPlanes, new MatOfInt(0), new Mat(), bHist, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(1), new Mat(), gHist, new MatOfInt(histSize), histRange, accumulate);
        Imgproc.calcHist(bgrPlanes, new MatOfInt(2), new Mat(), rHist, new MatOfInt(histSize), histRange, accumulate);

        int histW = 512, histH = 400;
        int binW = (int) Math.round((double) histW / histSize);

        Mat histImage = new Mat( histH, histW, CvType.CV_8UC3, new Scalar( 0,0,0) );

        Core.normalize(bHist, bHist, 0, histImage.rows(), Core.NORM_MINMAX);
        Core.normalize(gHist, gHist, 0, histImage.rows(), Core.NORM_MINMAX);
        Core.normalize(rHist, rHist, 0, histImage.rows(), Core.NORM_MINMAX);
        float[] bHistData = new float[(int) (bHist.total() * bHist.channels())];
        bHist.get(0, 0, bHistData);
        float[] gHistData = new float[(int) (gHist.total() * gHist.channels())];
        gHist.get(0, 0, gHistData);
        float[] rHistData = new float[(int) (rHist.total() * rHist.channels())];
        rHist.get(0, 0, rHistData);

        for( int i = 1; i < histSize; i++ ) {
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(bHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(bHistData[i])), new Scalar(255, 0, 0), 2);
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(gHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(gHistData[i])), new Scalar(0, 255, 0), 2);
            Imgproc.line(histImage, new Point(binW * (i - 1), histH - Math.round(rHistData[i - 1])),
                    new Point(binW * (i), histH - Math.round(rHistData[i])), new Scalar(0, 0, 255), 2);
        }

        return histImage;
    }

    /**
     * Equalise the histogram of the image
     * @param src the source image
     * @return the image with equalised histogram
     */
    public Mat equaliseHistogram(Mat src) {
        List<Mat> channels = new ArrayList<>(3);
        Core.split(src, channels);

        // Histogram equalization on each channel
        for (Mat channel : channels) {
            Imgproc.equalizeHist(channel, channel);
        }

        // Merge the channels back
        Mat outputImage = new Mat();
        Core.merge(channels, outputImage);

        // Release resources
        for (Mat mat : channels) {
            mat.release();
        }

        return outputImage;
    }

    /**
     * Adjust the colour balance of the image
     * @param src the source image
     * @param redGain the red gain factor
     * @param greenGain the green gain factor
     * @param blueGain the blue gain factor
     * @return the image with adjusted colour balance
     */
    public Mat colourBalanceAdjustment(Mat src, float redGain, float greenGain, float blueGain) {
        Mat result
                = new Mat(src.rows(), src.cols(),
                src.type());
        src.convertTo(result, CV_32F);

        for (int i = 0; i < src.rows(); i++) {
            for (int j = 0; j < src.cols(); j++) {
                double[] pixel = src.get(i, j);
                double blue = pixel[0] * blueGain;
                double green = pixel[1] * greenGain;
                double red = pixel[2] * redGain;

                // Check for valid pixel values
                blue = Math.min(255, Math.max(0, blue));
                green = Math.min(255, Math.max(0, green));
                red = Math.min(255, Math.max(0, red));

                result.put(i, j, blue, green, red);
            }
        }

        return result;
    }

    /**
     * Adjust the saturation of the image
     * @param src the source image
     * @param saturationAdjustment the saturation adjustment factor
     * @return the image with adjusted saturation
     */
    public Mat saturation(Mat src, double saturationAdjustment) {
        // convert from BGR to HSV color space
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(src, hsvImage, Imgproc.COLOR_BGR2HSV);

        // split into channels
        List<Mat> hsvChannels = new ArrayList<>(3);
        Core.split(hsvImage, hsvChannels);

        // Adjust the saturation channel (index 1)
        Mat saturationChannel = hsvChannels.get(1);

        double saturationFactor = (100.0 + saturationAdjustment) / 100.0;
        System.out.println(saturationFactor);
        saturationChannel.convertTo(saturationChannel, -1, saturationFactor, 0);

        // merge the channels back into the HSV image
        Core.merge(hsvChannels, hsvImage);

        // Convert back
        Mat result = new Mat();
        Imgproc.cvtColor(hsvImage, result, Imgproc.COLOR_HSV2BGR);

        return result;
    }

    /**
     * Apply gamma correction to the image
     * @param src the source image
     * @param gamma the gamma correction factor
     * @return the image with gamma correction applied
     */
    public Mat gammaCorrection(Mat src, double gamma) {
        // lookup table
        Mat lut = new Mat(1, 256, CvType.CV_8U);
        lut.setTo(new Scalar(0));

        byte[] lutData = new byte[(int)lut.total() * lut.channels()];
        for (int i = 0; i < 256; i++) {
            lutData[i] = (byte) Math.round(255 * Math.pow(i / 255.0, gamma));
        }
        lut.put(0, 0, lutData);

        // apply the loockup table
        Mat result = new Mat();
        Core.LUT(src, lut, result);

        return result;
    }

    /**
     * Apply white balance to the image
     * @param src the source image
     * @return the image with white balance applied
     */
    public Mat whiteBalance(Mat src) {
        Mat result
                = new Mat(src.rows(), src.cols(),
                src.type());
        // avg values
        Scalar avg = Core.mean(src);
        double avg_B = avg.val[0];
        double avg_G = avg.val[1];
        double avg_R = avg.val[2];

        // avg grayscale
        double avg_gray = (avg_B + avg_G + avg_R) / 3;

        // scaling factor
        double scale_B = avg_gray / avg_B;
        double scale_G = avg_gray / avg_G;
        double scale_R = avg_gray / avg_R;

        // apply them
        Core.multiply(src, new Scalar(scale_B, scale_G, scale_R), result);

        return result;
    }
}
