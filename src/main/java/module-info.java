module com.example.the7thcitadel {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.tvz.avuckovic.the7thcitadel to javafx.fxml;
    exports com.tvz.avuckovic.the7thcitadel;
}