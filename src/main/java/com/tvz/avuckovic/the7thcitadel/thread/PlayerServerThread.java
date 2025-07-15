package com.tvz.avuckovic.the7thcitadel.thread;

import com.tvz.avuckovic.the7thcitadel.RootController;
import com.tvz.avuckovic.the7thcitadel.model.GameState;
import javafx.application.Platform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
@RequiredArgsConstructor
public abstract class PlayerServerThread {
    protected final RootController rootController;

    protected void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());)
        {
            GameState receivedGameState = (GameState) ois.readObject();
            log.info("Current game state received!" + receivedGameState);
            Platform.runLater(() -> rootController.loadGameManually(receivedGameState));
            oos.writeObject(Boolean.TRUE);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
