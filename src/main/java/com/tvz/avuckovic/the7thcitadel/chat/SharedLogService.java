package com.tvz.avuckovic.the7thcitadel.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SharedLogService extends Remote {
    String REMOTE_OBJECT_NAME = "hr.tvz.rmi.shared.log.service";
    void saveLog(String message) throws RemoteException;
    List<String> getAllLogs() throws RemoteException;
    void clearLogs() throws RemoteException;
}
