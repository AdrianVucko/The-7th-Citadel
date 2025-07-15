package com.tvz.avuckovic.the7thcitadel.thread;

import com.tvz.avuckovic.the7thcitadel.RootController;
import com.tvz.avuckovic.the7thcitadel.jndi.ConfigurationKey;
import com.tvz.avuckovic.the7thcitadel.jndi.ConfigurationReader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class PlayerOneServerThread extends PlayerServerThread implements Runnable {
    public PlayerOneServerThread(RootController rootController) {
        super(rootController);
    }

    @Override
    public void run() {
        acceptRequestsFromPlayerTwo();
    }

    private void acceptRequestsFromPlayerTwo() {
        try (ServerSocket serverSocket = new ServerSocket(
                ConfigurationReader.getIntegerValue(ConfigurationKey.PLAYER_ONE_SERVER_PORT)))
        {
            log.info("Player two server listening on port: " + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                log.info("Player one client connected from port: " + clientSocket.getPort());
                new Thread(() ->  processSerializableClient(clientSocket)).start();
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
}
