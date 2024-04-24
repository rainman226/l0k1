package ro.uvt.loki.services;

import javafx.scene.image.Image;

import org.bytedeco.opencv.opencv_core.MatVector;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.opencv.core.Core.addWeighted;
import static org.opencv.core.CvType.CV_32F;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static ro.uvt.loki.HelperFunctions.imshow;


public class EnchantmentService {
    public Mat increaseBrightness(Mat source, double alpha, double beta) {
        //alpha contrast, beta brightness
        source.convertTo(source, -1, alpha, beta);
        //HighGui.imshow("Test", source);

        return source;
    }

//    public Mat sharpen(Mat source) {
//        Mat destination
//                = new Mat(source.rows(), source.cols(),
//                source.type());
//        GaussianBlur(source, destination, new Size(0, 0), 10);
//        addWeighted(source, 1.5, destination, -0.5, 0, destination);
//        //imshow("test", destination);
//        return destination;
//    }

//    public Mat contrastStretch(Mat source) {
//        Mat destination
//                = new Mat(source.rows(), source.cols(),
//                source.type());
//        if (source.empty())
//            return source;
//        List<Mat> bgrPlanes = new ArrayList<>();
//        Core.split(source, bgrPlanes);
//        for (int i = 0; i < bgrPlanes.size(); i++) {
//            Core.normalize(bgrPlanes.get(i), bgrPlanes.get(i), 0, 255, Core.NORM_MINMAX);
//        }
//        Core.merge(bgrPlanes, destination);
//        imshow("test", destination);
//        return destination;
//    }

    public Mat calculateHistogram(Mat source) {

        List<Mat> bgrPlanes = new ArrayList<>();
        Core.split(source, bgrPlanes);

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

        Imgcodecs.imwrite("C:\\Users\\dota2\\Desktop\\resources\\output\\original.jpg",
                source);

        Imgcodecs.imwrite("C:\\Users\\dota2\\Desktop\\resources\\output\\hist.jpg",
                histImage);

        return histImage;
    }

    public Mat equaliseHistogram(Mat src) {
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        Mat dst = new Mat();
        Imgproc.equalizeHist( src, dst );
        Imgcodecs.imwrite("C:\\Users\\dota2\\Desktop\\resources\\output\\output.jpg",
                dst);
        return dst;
    }

    public Mat blur(Mat src) {
        Mat destination
                = new Mat(src.rows(), src.cols(),
                src.type());
        Imgproc.blur(src, destination, new Size(250, 250));
        return destination;
    }

    public Mat colourBalanceAdjustment(Mat src, float redGain, float greenGain, float blueGain) {
        Mat destination
                = new Mat(src.rows(), src.cols(),
                src.type());
        src.convertTo(destination, CV_32F);

//        float redGain = 3.0f; 3.0390685
//        float greenGain = 6.0f; 2.0354838
//        float blueGain = 4.0f; 3.9709682

        for (int i = 0; i < src.rows(); i++) {
            for (int j = 0; j < src.cols(); j++) {
                double[] pixel = src.get(i, j);
                double blue = pixel[0] * blueGain;
                double green = pixel[1] * greenGain;
                double red = pixel[2] * redGain;

                // Ensure pixel values are within the valid range [0, 255]
                blue = Math.min(255, Math.max(0, blue));
                green = Math.min(255, Math.max(0, green));
                red = Math.min(255, Math.max(0, red));

                destination.put(i, j, blue, green, red);
            }
        }

        return destination;
    }

    public Mat saturation(Mat src, double saturationAdjustment) {
        // Convert image from BGR to HSV color space
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(src, hsvImage, Imgproc.COLOR_BGR2HSV);

        // Split the HSV image into separate channels
        List<Mat> hsvChannels = new ArrayList<>(3);
        Core.split(hsvImage, hsvChannels);

        // Adjust the saturation channel (index 1)
        // You can adjust the saturation factor here (e.g., multiply by a scalar)
        Mat saturationChannel = hsvChannels.get(1);

        double saturationFactor = (100.0 + saturationAdjustment) / 100.0;
        System.out.println(saturationFactor);
        saturationChannel.convertTo(saturationChannel, -1, saturationFactor, 0);

        // Merge the channels back into the HSV image
        Core.merge(hsvChannels, hsvImage);

        // Convert the image from HSV back to BGR color space
        Mat result = new Mat();
        Imgproc.cvtColor(hsvImage, result, Imgproc.COLOR_HSV2BGR);

        return result;
    }

    public Mat gammaCorrection(Mat src, double gamma) {
        // Create a lookup table for gamma correction
        Mat lut = new Mat(1, 256, CvType.CV_8U);
        lut.setTo(new Scalar(0));

        byte[] lutData = new byte[(int)lut.total() * lut.channels()];
        for (int i = 0; i < 256; i++) {
            lutData[i] = (byte) Math.round(255 * Math.pow(i / 255.0, gamma));
        }
        lut.put(0, 0, lutData);

        // Apply the lookup table to the source image
        Mat result = new Mat();
        Core.LUT(src, lut, result);

        return result;
    }

    public Mat whiteBalance(Mat src) {
        // Calculate the average values for each color channel
        Scalar avg = Core.mean(src);
        double avg_B = avg.val[0];
        double avg_G = avg.val[1];
        double avg_R = avg.val[2];

        // Calculate the average grayscale value
        double avg_gray = (avg_B + avg_G + avg_R) / 3;

        // Compute scaling factors for each channel
        double scale_B = avg_gray / avg_B;
        double scale_G = avg_gray / avg_G;
        double scale_R = avg_gray / avg_R;

        // Apply the scaling factors to each channel
        Core.multiply(src, new Scalar(scale_B, scale_G, scale_R), src);

        return src;
    }
}
