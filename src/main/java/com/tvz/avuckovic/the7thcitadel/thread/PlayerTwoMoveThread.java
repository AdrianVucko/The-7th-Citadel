package com.tvz.avuckovic.the7thcitadel.thread;

import com.tvz.avuckovic.the7thcitadel.jndi.ConfigurationKey;
import com.tvz.avuckovic.the7thcitadel.jndi.ConfigurationReader;
import com.tvz.avuckovic.the7thcitadel.model.GameState;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class PlayerTwoMoveThread implements Runnable {

    private final GameState gameState;

    public PlayerTwoMoveThread(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void run() {
        sendRequest();
    }

    private void sendRequest() {
        try (Socket clientSocket = new Socket(
                ConfigurationReader.getStringValue(ConfigurationKey.HOSTNAME),
                ConfigurationReader.getIntegerValue(ConfigurationKey.PLAYER_ONE_SERVER_PORT)))
        {
            log.info("Client is connecting to " + clientSocket.getInetAddress() + ":" +clientSocket.getPort());

            sendSerializableRequest(clientSocket);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendSerializableRequest(Socket client) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        oos.writeObject(gameState);
        log.info("Game state received confirmation: " + ois.readObject());
    }
}
