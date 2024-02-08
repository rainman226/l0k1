package ro.uvt.loki;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import ro.uvt.loki.services.EnchantmentService;

import static org.opencv.imgcodecs.Imgcodecs.imread;


public class Demo2 {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat src = imread("C:\\Users\\dota2\\Desktop\\resources\\1.jpg");
        Mat destination
                = new Mat(src.rows(), src.cols(),
                src.type());
        if (src.empty())
            return;
        // Show source image
        HighGui.imshow("Source Image", src);
        //HighGui.waitKey();
        EnchantmentService service = new EnchantmentService();
        destination = service.increaseBrightness(src);
        Imgcodecs.imwrite("C:\\Users\\dota2\\Desktop\\resources\\output\\output.jpg",
                destination);
        //HighGui.waitKey();
        System.exit(0);
    }
}
