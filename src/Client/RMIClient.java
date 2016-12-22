/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import DirectoryService.RemoteServiceInterface;
import DirectoryService.ServerEntry;
import DirectoryService.ServerMonitorListener;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author luism
 */
public class RMIClient extends UnicastRemoteObject implements ServerMonitorListener, Runnable {

    private String addr;
    private int port;
    private ClientCommands commands;
    
    RemoteServiceInterface rmiService;
    String output;

    public RMIClient(String addr, int port, ClientCommands cmd) throws RemoteException
    {
        this.addr = addr;
        this.port = port;
        this.commands = cmd;
    }
    
    @Override
    public void run(){
        try
        {
            String registration = "rmi://" + addr + "/" + RemoteServiceInterface.SERVICE_NAME;
            Remote remoteService = Naming.lookup(registration);
            rmiService = (RemoteServiceInterface) remoteService;
            rmiService.addObserver(this);
            
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
    
    public RemoteServiceInterface getService(){
        return rmiService;
    }

    @Override
    public void printServers() throws RemoteException {
        if (commands.getLastCommand().equalsIgnoreCase("LIST")){
            StringBuilder out = new StringBuilder();
            //out.append("Lista nos quais nao esta autenticado: ");
            boolean atLeastOneElementFlag = false;
            ArrayList<ServerEntry> serverList = rmiService.getServerList();
            Iterator sit = serverList.iterator();
            while (sit.hasNext()){
                ServerEntry se = (ServerEntry)sit.next();
                if (!se.existsClient(commands.getClientName())){
                //out.append("\n\t" + se.getName() + "\t" + se.getAddr().getHostAddress() + "\t" + se.getPort());
                    out.append("\n\t" + se.toString());
                    atLeastOneElementFlag = true;
                }
            }
            if (atLeastOneElementFlag){
                output = "Lista nos quais nao esta autenticado: " + out.toString();
            }
            else output = "";
            //System.out.println(output);
            commands.updateServerList(output);
        }
    }
}

