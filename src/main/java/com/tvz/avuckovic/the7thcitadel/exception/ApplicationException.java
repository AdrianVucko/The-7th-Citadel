package com.tvz.avuckovic.the7thcitadel.exception;

import javafx.scene.control.Alert;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    private Alert.AlertType alertType;
    public ApplicationException(String message) {
        super(message);
        alertType = Alert.AlertType.ERROR;
    }

    public ApplicationException(String message, Alert.AlertType alertType) {
        super(message);
        this.alertType = alertType;
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
