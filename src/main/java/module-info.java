module com.example.the7thcitadel {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.the7thcitadel to javafx.fxml;
    exports com.example.the7thcitadel;
}