package com.tvz.avuckovic.the7thcitadel.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatRemoteService extends Remote {
    String REMOTE_OBJECT_NAME = "hr.tvz.rmi.chat.service";
    void sendChatMessage(String message) throws RemoteException;
    List<String> getAllChatMessages() throws RemoteException;
}