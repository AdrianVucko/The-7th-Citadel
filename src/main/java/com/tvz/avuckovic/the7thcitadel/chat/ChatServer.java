package com.tvz.avuckovic.the7thcitadel.chat;

import com.tvz.avuckovic.the7thcitadel.jndi.ConfigurationKey;
import com.tvz.avuckovic.the7thcitadel.jndi.ConfigurationReader;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ChatServer {

    private static final int RANDOM_PORT_HINT = 0;

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(ConfigurationReader.getIntegerValue(
                    ConfigurationKey.RMI_PORT));
            ChatRemoteService chatRemoteService = new ChatRemoteServiceImpl();
            SharedLogService sharedLogService = new SharedLogServiceImpl();
            ChatRemoteService chatSkeleton = (ChatRemoteService) UnicastRemoteObject.exportObject(chatRemoteService,
                    RANDOM_PORT_HINT);
            SharedLogService sharedLogSkeleton = (SharedLogService) UnicastRemoteObject.exportObject(sharedLogService,
                    RANDOM_PORT_HINT);
            registry.rebind(ChatRemoteService.REMOTE_OBJECT_NAME, chatSkeleton);
            registry.rebind(SharedLogService.REMOTE_OBJECT_NAME, sharedLogSkeleton);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
