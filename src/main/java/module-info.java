module ro.uvt.loki {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.bytedeco.javacv;
    requires org.bytedeco.opencv;
    requires java.desktop;
    requires org.slf4j;


    opens ro.uvt.loki to javafx.fxml;
    opens ro.uvt.loki.controllers to javafx.fxml;
    opens ro.uvt.loki.controllers.tutorial to javafx.fxml;
    exports ro.uvt.loki;
}

