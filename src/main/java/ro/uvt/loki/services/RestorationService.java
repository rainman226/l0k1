package ro.uvt.loki.services;

import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.io.File;

import static ro.uvt.loki.HelperFunctions.imshow;
import static ro.uvt.loki.HelperFunctions.toFXImage;

public class RestorationService {
    public Mat inpaintImageMaskSelected(Mat source) {
//        Mat outputImage = new Mat();
//        String filePath = "C:\\Users\\dota2\\Desktop\\resources\\cat_mask.png";
//        Mat mask = Imgcodecs.imread(filePath, Imgcodecs.IMREAD_GRAYSCALE);
//        //double inpaintRadius = 3;
//        int inpaintMethod = Photo.INPAINT_TELEA;
//        Photo.inpaint(source, mask, outputImage, 3, inpaintMethod);
//
//        return outputImage;
        Mat output = new Mat();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Mask file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp", "*.jpeg")
        );

        File maskFile = fileChooser.showOpenDialog(null);

        if (maskFile != null) {
            String maskPath = maskFile.toURI().getPath().substring(1);
            Mat mask = Imgcodecs.imread(maskPath, Imgcodecs.IMREAD_GRAYSCALE);
            if (mask.empty()) {
                System.err.println("Cannot read image: " + maskPath );
                System.exit(0);
            }

            //double inpaintRadius = 3;
            int inpaintMethod = Photo.INPAINT_TELEA;
            Photo.inpaint(source, mask, output, 3, inpaintMethod);

        }
        return output;
    }
}
