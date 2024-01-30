module ro.uvt.loki {
    requires javafx.controls;
    requires javafx.fxml;


    opens ro.uvt.loki to javafx.fxml;
    exports ro.uvt.loki;
}