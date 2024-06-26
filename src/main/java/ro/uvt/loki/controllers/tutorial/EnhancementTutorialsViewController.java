package ro.uvt.loki.controllers.tutorial;

import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

public class EnhancementTutorialsViewController {
    public void tutorialHistogramEqualisation() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Histogram Equalization Tutorial");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(1080);
        imageView.setFitHeight(720);
        Button leftButton = new Button("<");
        Button rightButton = new Button(">");

        String basePath = "D:/Projects/l0k1/loki/src/main/resources/images/tutorials/1ench/3histogrameq";
        Image[] images = new Image[5];
        for (int i = 0; i < 5; i++) {
            images[i] = new Image("file:///" + basePath + "/" + (i + 1) + ".png");
        }
        System.out.println(images[0].getUrl());
        imageView.setImage(images[0]);
        int[] currentImageIndex = {0};

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

        HBox buttonsBox = new HBox(10, leftButton, rightButton);
        buttonsBox.setStyle("-fx-alignment: center;");
        VBox vbox = new VBox(10, imageView, buttonsBox);
        vbox.setStyle("-fx-padding: 10;");

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);
        dialog.setResultConverter(dialogButton -> {
            return null;
        });

        dialog.showAndWait();
    }
}
