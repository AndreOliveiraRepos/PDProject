/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import DirectoryService.RemoteServiceInterface;
import DirectoryService.Manager.ServerEntry;
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
    private Client commands;
    private ClientUI view;
    
    RemoteServiceInterface rmiService;
    String output;

    public RMIClient(ClientUI v, String addr, int port, Client cmd) throws RemoteException
    {
        this.addr = addr;
        this.port = port;
        this.commands = cmd;
        this.view = v;
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
    
    public void terminate() throws RemoteException{
        rmiService.removeObserver(this);
        
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
            view.printContent(output);
            
        }
    }
}

