module com.example.the7thcitadel {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires static lombok;

    opens com.tvz.avuckovic.the7thcitadel to javafx.fxml;
    exports com.tvz.avuckovic.the7thcitadel;
    exports com.tvz.avuckovic.the7thcitadel.component;
    exports com.tvz.avuckovic.the7thcitadel.controller;
    opens com.tvz.avuckovic.the7thcitadel.controller to javafx.fxml;
    opens com.tvz.avuckovic.the7thcitadel.model to javafx.base;
}