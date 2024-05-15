package ro.uvt.loki.dialogControllers;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class SaturationInputController {
    public static String[] showSaturationInputDialog() {
        // Create a grid pane for multiple inputs
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Create text fields for user input
        TextField param1Field = new TextField();
        TextField param2Field = new TextField();

        // Add labels and fields to the grid
        grid.add(new Label("Radius:"), 0, 0);
        grid.add(param1Field, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(param2Field, 1, 1);

        // Create the dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Saturation values");
        dialog.setHeaderText("Enter the radius and amount:");
        dialog.getDialogPane().setContent(grid);

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            // Get the parameters from the text fields
            String param1 = param1Field.getText();
            String param2 = param2Field.getText();

            return new String[]{param1, param2};

        }
        else return null;
    }
}
