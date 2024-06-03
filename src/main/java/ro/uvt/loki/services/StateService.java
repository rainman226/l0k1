package ro.uvt.loki.services;

import org.opencv.core.Mat;

import java.util.Stack;

public class StateService {
    private Mat originalImage;
    private Mat processedImage;
    private Stack<Mat> historyStack = new Stack<>();

    public Mat getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(Mat originalImage) {
        this.originalImage = originalImage;
        this.processedImage = originalImage.clone();
        this.historyStack.clear();
        System.out.println("Am setat imaginea originala boss" + originalImage);
    }

    public Mat getProcessedImage() {
        return processedImage;
    }

    public void setProcessedImage(Mat processedImage) {
        this.historyStack.push(this.processedImage.clone());
        this.processedImage = processedImage;
    }

    public Stack<Mat> getHistoryStack() {
        return historyStack;
    }

    public void undo() {
        if (!historyStack.isEmpty()) {
            processedImage = historyStack.pop();
        }
    }
}
