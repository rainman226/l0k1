package ro.uvt.loki.dialogControllers;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class SimpleInputController {
    public static String saturationInputDialog() {
        // Create a grid pane for multiple inputs
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Create text fields for user input
        TextField param1Field = new TextField();

        // Add labels and fields to the grid
        grid.add(new Label("Value:"), 0, 0);
        grid.add(param1Field, 1, 0);

        // Create the dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Enter saturation value (between -100 and 100):");
        dialog.getDialogPane().setContent(grid);

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            // Get the parameters from the text fields
            String param1 = param1Field.getText();

            return param1;

        }
        else return null;
    }
}
