package ServerMonitor;

import DirectoryService.ServerMonitorListener;
import DirectoryService.RemoteServiceInterface;
import DirectoryService.ServerEntry;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;

public class ServerMonitor extends UnicastRemoteObject implements ServerMonitorListener{
    
    private static RemoteServiceInterface serverMonitor;
    
    public ServerMonitor() throws RemoteException{}
    
    public static void main(String[] args) throws RemoteException {
        ServerMonitor observer = new ServerMonitor();
        
        if (args.length != 2) {
            System.out.println("Sintaxe: java ServerMonitor <DirectoryServer ip> <DirectoryServer Port>\n");
            return;
        }
        String addr = args[0];
        try
        {
            String registration = "rmi://" + addr + "/" + RemoteServiceInterface.SERVICE_NAME;
            Remote remoteService = Naming.lookup(registration);
            serverMonitor = (RemoteServiceInterface) remoteService;
            serverMonitor.addObserver(observer);
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
    
    public void updateView(String s){
        try {
            Runtime.getRuntime().exec("cls");
        } catch (IOException ex) {}
        System.out.println(s);
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
        updateView(out.toString());
    }
}
