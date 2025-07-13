package com.tvz.avuckovic.the7thcitadel.chat;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class SharedLogServiceImpl implements SharedLogService{
    private final List<String> logs;

    public SharedLogServiceImpl() {
        logs = new ArrayList<>();
    }

    @Override
    public void saveLog(String message) throws RemoteException {
        logs.add(message);
    }

    @Override
    public List<String> getAllLogs() throws RemoteException {
        return logs;
    }

    @Override
    public void clearLogs() throws RemoteException {
        logs.clear();
    }
}
