package ro.uvt.loki;

import org.bytedeco.opencv.opencv_core.Mat;
import ro.uvt.loki.services.EnchantmentService;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static ro.uvt.loki.HelperFunctions.imshow;

public class Demo2 {
    public static void main(String[] args) {
        Mat src = imread("C:\\Users\\dota2\\Desktop\\resources\\1.jpg");
        if (src.data().isNull())
            return;
        // Show source image
        imshow("Source Image", src);
        EnchantmentService service = new EnchantmentService();
        src = service.sharpen(src);
    }
}
