package ro.uvt.loki.services;

import javafx.scene.image.Image;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.addWeighted;
import static org.opencv.imgproc.Imgproc.GaussianBlur;



public class EnchantmentService {
    public Mat increaseBrightness(Mat source, double alpha, double beta) {
        source.convertTo(source, -1, alpha, beta);
        //HighGui.imshow("Test", source);

        return source;
    }

    public Mat sharpen(Mat source) {
        Mat destination
                = new Mat(source.rows(), source.cols(),
                source.type());
        GaussianBlur(source, destination, new Size(0, 0), 10);
        addWeighted(source, 1.5, destination, -0.5, 0, destination);
        //imshow("test", destination);
        return destination;
    }

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
}
