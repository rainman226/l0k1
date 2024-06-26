package ro.uvt.loki.controllers.tutorial;

import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EnhancementTutorialsViewController {
    public void tutorialHistogramEqualisation() {
        // Create a dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Histogram Equalization Tutorial");

        // Create ImageViews and buttons
        ImageView imageView = new ImageView();
        imageView.setFitWidth(1080); // Set width to fit the dialog size minus space for buttons
        imageView.setFitHeight(720);
        Button leftButton = new Button("<");
        Button rightButton = new Button(">");

        // Set up the image paths
        String basePath = "D:/Projects/l0k1/loki/src/main/resources/images/tutorials/1ench/3histogrameq";
        Image[] images = new Image[5];
        for (int i = 0; i < 5; i++) {
            images[i] = new Image("file:///" + basePath + "/" + (i + 1) + ".png");
        }
        System.out.println(images[0].getUrl());
        // Start showing the first image
        imageView.setImage(images[0]);
        int[] currentImageIndex = {0}; // Use array to modify in lambda

        // Configure buttons
        leftButton.setOnAction(e -> {
            if (currentImageIndex[0] > 0) {
                currentImageIndex[0]--;
                imageView.setImage(images[currentImageIndex[0]]);
            }
        });

        rightButton.setOnAction(e -> {
            if (currentImageIndex[0] < images.length - 1) {
                currentImageIndex[0]++;
                imageView.setImage(images[currentImageIndex[0]]);
            }
        });

        // Layout setup
        HBox buttonsBox = new HBox(10, leftButton, rightButton);
        buttonsBox.setStyle("-fx-alignment: center;");
        VBox vbox = new VBox(10, imageView, buttonsBox);
        vbox.setStyle("-fx-padding: 10;");

        // Set the dialog pane
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);
        dialog.setResultConverter(dialogButton -> {
            return null; // Handle close event
        });

        // Show dialog
        dialog.showAndWait();
    }
}
