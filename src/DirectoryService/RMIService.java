package DirectoryService;

import DirectoryService.Manager.ServerEntry;
import DirectoryService.Manager.ServerManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RMIService extends UnicastRemoteObject implements RemoteServiceInterface, Runnable{
    
    private ServerManager serverManager;
    private static ArrayList<ServerMonitorListener> observers;
    
    public RMIService(ServerManager sm) throws RemoteException {
        serverManager = sm;
        observers = new ArrayList<ServerMonitorListener>();
    }
    
    @Override
    public ArrayList<ServerEntry> getServerList() throws RemoteException {
        return serverManager.getServerList();
    }
    
    @Override
    public void addObserver(ServerMonitorListener observer) throws RemoteException {
        observers.add(observer);
        notifyObservers();
    }
    
    @Override
    public void removeObserver(ServerMonitorListener observer) throws RemoteException {
        if (!observers.isEmpty()){
            observers.remove(observer);
        }
    }
    
    public void notifyObservers() throws RemoteException{
        //System.out.println("Observadores: " + observers.size());
        for (ServerMonitorListener observer : observers){
            //if (observer.wait())
            if (observer != null)
                observer.printServers();
        }
    }

    @Override
    public void run() {
        try
        {
            Registry r;
            try
            {
                System.out.println("Tentativa de lancamento do registry no porto " + 
                                    Registry.REGISTRY_PORT + "...");
                r = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                System.out.println("Registry lancado!");
                                
            }catch(RemoteException e){
                System.out.println("Registry provavelmente ja' em execucao!");
                r = LocateRegistry.getRegistry();          
            }
            
            RMIService directoryService = new RMIService(serverManager);
            
            System.out.println("Servico RMI criado e em execucao ("+directoryService.getRef().remoteToString()+"...");
             
            r.bind("DirectoryService", directoryService);     
                   
            System.out.println("Servico DirectoryService registado no registry...");
            
        }catch(RemoteException e){
            System.out.println("Erro remoto - " + e);
            System.exit(1);
        }catch(Exception e){
            System.out.println("Erro - " + e);
            System.exit(1);
        }          
    }  
}
