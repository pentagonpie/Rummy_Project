module com.rummy.ui.rummyui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires com.almasb.fxgl.all;
    requires java.rmi;
    requires com.rummy.shared;

    opens com.rummy.ui.rummyui to javafx.fxml;
    exports com.rummy.ui.rummyui;
}