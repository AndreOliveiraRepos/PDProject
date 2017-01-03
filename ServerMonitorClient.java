package ServerMonitor;

import DirectoryService.ServerMonitorListener;
import DirectoryService.RemoteServiceInterface;
import DirectoryService.Manager.ServerEntry;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;

public class ServerMonitorClient extends UnicastRemoteObject implements ServerMonitorListener
{
    private String addr;
    private int port;
    ServerMonitor view;
    RemoteServiceInterface serverMonitor;

    public ServerMonitorClient(String addr, int port, ServerMonitor v) throws RemoteException
    {
        this.addr = addr;
        this.port = port;
        this.view = v;
        
        try
        {
            String registration = "rmi://" + addr + "/" + RemoteServiceInterface.SERVICE_NAME;
            Remote remoteService = Naming.lookup(registration);
            serverMonitor = (RemoteServiceInterface) remoteService;
            serverMonitor.addObserver(this);
        }
        catch (NotBoundException e){
            System.out.println("Não existe servico disponivel! ");
        }
        catch (RemoteException e){
            System.out.println("Erro no RMI: " + e);
        }
        catch (Exception e){
            System.out.println("Erro: " + e);
        }
    }
    
    @Override
    public void printServers() throws RemoteException{
        StringBuilder out = new StringBuilder();
        ArrayList<ServerEntry> serverList = serverMonitor.getServerList();
        Iterator sit = serverList.iterator();
        while (sit.hasNext()){
            ServerEntry se = (ServerEntry)sit.next();
            out.append("\n" + se.getName() + "\t" + se.getAddr().getHostAddress() + "\t" + se.getPort());
            Iterator cit = se.getConnectedClients().iterator();
            while (cit.hasNext()){
                out.append("\n\t" + (String)cit.next());
            }
        }
        view.updateView(out.toString());
    }
    
}
