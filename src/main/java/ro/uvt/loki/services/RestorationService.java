package ro.uvt.loki.services;

import javafx.stage.FileChooser;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ro.uvt.loki.HelperFunctions.imshow;

public class RestorationService {
    /**
     * Inpaints an image using the Telea algorithm (mask selected by the user).
     * @param source The source image as a Mat object.
     * @return  The inpainted image as a Mat object.
     */
    public Mat inpaintImageMaskSelected(Mat source) {
        Mat output = new Mat();

        // Select mask file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Mask file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp", "*.jpeg")
        );

        File maskFile = fileChooser.showOpenDialog(null);

        if (maskFile != null) {
            String maskPath = maskFile.toURI().getPath().substring(1);      // Get the path of the selected file
            Mat mask = Imgcodecs.imread(maskPath, Imgcodecs.IMREAD_GRAYSCALE);        // Read the mask file
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

    /**
     * Inpaints an image using the Telea algorithm (mask generated using createMaskForInpainting).
     * @param sourceImage The source image as a Mat object.
     * @return The inpainted image as a Mat object.
     */
    public Mat inpaintImageComputeMask(Mat sourceImage) {
        Mat mask = createMaskForInpainting(sourceImage);
        Mat output = new Mat();

        int inpaintMethod = Photo.INPAINT_TELEA;
        Photo.inpaint(sourceImage, mask, output, 3, inpaintMethod);

        return output;
    }

    /**
     * Creates a mask for inpainting based on the source image.
     * @param sourceImage The source image as a Mat object.
     * @return The mask as a Mat object.
     */
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

    /**
     * Applies Contrast Limited Adaptive Histogram Equalization (CLAHE) to an image.
     * @param sourceImage The source image as a Mat object.
     * @return The image with CLAHE applied.
     */
    public Mat applyCLAHE(Mat sourceImage) {
        // We split the image into its channels
        List<Mat> channels = new ArrayList<>();
        Core.split(sourceImage, channels);

        // Apply CLAHE to each channel
        CLAHE clahe = Imgproc.createCLAHE();
        // TODO Adjust the clip limit and tile grid size as needed
        clahe.setClipLimit(2.0);
        clahe.setTilesGridSize(new Size(8, 8));

        for (int i = 0; i < channels.size(); i++) {
            Mat channel = channels.get(i);
            clahe.apply(channel, channel);
        }

        // Merge the channels back to form the final image
        Mat outputImage = new Mat();
        Core.merge(channels, outputImage);

        // Clean up
        for (Mat mat : channels) {
            mat.release();
        }

        return outputImage;
    }

    /**
     * Applies adaptive thresholding to an image.
     *
     * @param sourceImage The source image as a Mat object, expected to be in grayscale.
     * @return The thresholded image as a Mat object.
     */
    public Mat applyAdaptiveThreshold(Mat sourceImage) {
        Mat grayImage = new Mat();

        // Check if the image is already in grayscale
        if (sourceImage.channels() > 1) {
            Imgproc.cvtColor(sourceImage, grayImage, Imgproc.COLOR_BGR2GRAY);
        } else {
            grayImage = sourceImage.clone(); // Use the original if it's already grayscale
        }

        Mat outputImage = new Mat();
        // Apply adaptive threshold
        /*
            * Parameters:
            * 1. Source image
            * 2. Destination image
            * 3. Maximum intensity value
            * 4. Adaptive method
            * 5. Threshold type
            * 6. Block size
            * 7. Constant subtracted from the mean
         */
        Imgproc.adaptiveThreshold(grayImage, outputImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY, 11, 2);

        return outputImage;
    }
}
