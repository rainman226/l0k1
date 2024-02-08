package ro.uvt.loki;


import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import ro.uvt.loki.services.EnchantmentService;

import java.util.ArrayList;
import java.util.List;

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

        EnchantmentService service = new EnchantmentService();
//        Imgcodecs.imwrite("C:\\Users\\dota2\\Desktop\\resources\\output\\output.jpg",
//                destination);



    }
}
