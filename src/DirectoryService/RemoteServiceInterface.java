package DirectoryService;

import DirectoryService.Manager.ServerEntry;
import java.util.ArrayList;

public interface RemoteServiceInterface extends java.rmi.Remote{
    
    public static final String SERVICE_NAME = "DirectoryService";
    
    public ArrayList<ServerEntry> getServerList() throws java.rmi.RemoteException;
    public void addObserver(ServerMonitorListener observer) throws java.rmi.RemoteException;
    public void removeObserver(ServerMonitorListener observer) throws java.rmi.RemoteException;
}
