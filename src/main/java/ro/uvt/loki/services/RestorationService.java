package ro.uvt.loki.services;

import javafx.stage.FileChooser;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ro.uvt.loki.HelperFunctions.imshow;

public class RestorationService {
    public Mat inpaintImageMaskSelected(Mat source) {
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
            imshow("Mask", mask);
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

    public Mat inpaintImageComputeMask(Mat sourceImage) {
        Mat mask = createMaskForInpainting(sourceImage);
        Mat output = new Mat();
        //double inpaintRadius = 3;
        int inpaintMethod = Photo.INPAINT_TELEA;
        Photo.inpaint(sourceImage, mask, output, 3, inpaintMethod);
        return output;
    }

    public Mat createMaskForInpainting(Mat sourceImage) {
        Mat mask = new Mat(sourceImage.size(), CvType.CV_8UC1, new Scalar(0)); // Initialize mask with zeros

        // Convert to grayscale to simplify analysis
        Mat gray = new Mat();
        Imgproc.cvtColor(sourceImage, gray, Imgproc.COLOR_BGR2GRAY);

        byte[] grayData = new byte[(int) (gray.total())];
        gray.get(0, 0, grayData);

        for (int i = 0; i < grayData.length; i++) {
            int pixelValue = grayData[i] & 0xFF; // Get pixel value (unsigned)

            // Adjust this threshold according to the specifics of damage
            if (pixelValue < 30 || pixelValue > 225) { // Assume very dark or very light pixels might be damaged
                grayData[i] = (byte) 255;
            } else {
                grayData[i] = 0;
            }
        }
        mask.put(0, 0, grayData);
        //imshow("Mask", mask);
        return mask;
    }
}
