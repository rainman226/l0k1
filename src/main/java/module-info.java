module ro.uvt.loki {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.bytedeco.javacv;
    requires org.bytedeco.opencv;
    requires java.desktop;


    opens ro.uvt.loki to javafx.fxml;
    opens ro.uvt.loki.dialogControllers to javafx.fxml;
    opens ro.uvt.loki.controllers to javafx.fxml;
    exports ro.uvt.loki;
}

