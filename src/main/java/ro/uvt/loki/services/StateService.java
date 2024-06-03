package ro.uvt.loki.services;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.opencv.core.Mat;

import java.util.Stack;

public class StateService {
    private static StateService instance;

    private Mat originalImage;
    private final ObjectProperty<Mat> processedImage = new SimpleObjectProperty<>();    // The processed image property
    private final Stack<Mat> historyStack = new Stack<>();

    private StateService() {}

    // Singleton pattern
    public static StateService getInstance() {
        if (instance == null) {
            instance = new StateService();
        }
        return instance;
    }

    public Mat getOriginalImage() {
        return originalImage;
    }

    // Set the original image and reset the processed image
    public void setOriginalImage(Mat originalImage) {
        this.originalImage = originalImage;
        this.processedImage.set(originalImage.clone());
        this.historyStack.clear();
    }

    public Mat getProcessedImage() {
        return processedImage.get();
    }

    // Return the processed image property
    public ObjectProperty<Mat> processedImageProperty() {
        return processedImage;
    }

    public void setProcessedImage(Mat processedImage) {
        this.historyStack.push(this.processedImage.get().clone());
        this.processedImage.set(processedImage);
    }

    public void undo() {
        if (!historyStack.isEmpty()) {
            processedImage.set(historyStack.pop());
        }
    }
}
