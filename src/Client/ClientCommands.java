package Client;

import DirectoryService.RemoteServiceInterface;
import DirectoryService.ServerEntry;
import DirectoryService.ServerMonitorListener;
import common.Msg;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientCommands /*extends UnicastRemoteObject implements ServerMonitorListener*/{
    
    private static final String LIST = "LIST";
    private static final String MSG = "MSG";
    private static final String USERS = "USERS";
    private static final String MSGTO = "MSGTO";
    
    private ClientTcpHandler tcpHandler;
    private ClientUdpHandler udpHandler;
    private RMIClient rmiClient;
    
    private String lastCommand;
    private Client view;
    private InetAddress directoryServerAddr;
    private Integer directoryServerPort;
    
    
    public ClientCommands(Client v, ClientUdpHandler udpHandler, ClientTcpHandler tcpHandler,
            InetAddress dirServerAddr, Integer dirServerPort) throws RemoteException
    {
        this.directoryServerAddr = dirServerAddr;
        this.directoryServerPort = dirServerPort;
        this.tcpHandler = tcpHandler;
        this.udpHandler = udpHandler;
        
        this.view = v;
        this.lastCommand = "";
    }
    
    public String processRequest(Msg msg) throws UnknownHostException, IOException{
        lastCommand = msg.getMsg();
        
        if (rmiClient == null){
            System.out.println("Servico rmi iniciado! ");
            this.rmiClient = new RMIClient(directoryServerAddr.getHostAddress() ,directoryServerPort, this);
            rmiClient.run();
        }
        String[] args = msg.getMsg().split("\\s");
        
        if (args[0].equalsIgnoreCase(LIST)
            || args[0].equalsIgnoreCase(MSG)
            || args[0].equalsIgnoreCase(USERS)
            || args[0].equalsIgnoreCase(MSGTO))
        {
            try {
                return udpHandler.sendRequest(msg);
            } catch (IOException ex) {
                System.out.println("Erro ao enviar pedido UDP! " + ex);
            } catch (ClassNotFoundException ex) {
                System.out.println(ex);
            }
        }
        else if (args[0].equalsIgnoreCase("CONNECT")){
            //if (args.length == 3){
                //connect 127.0.0.1 7001
                //tcpHandler.connectToServer(InetAddress.getByName(args[1]), Integer.parseInt(args[2]));
                tcpHandler.connectToServer(InetAddress.getByName("127.0.0.1"), Integer.parseInt("7001"));
            try {
                System.out.println(
                        tcpHandler.sendRequest("Pedido teste")
                );
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientCommands.class.getName()).log(Level.SEVERE, null, ex);
            }
            //} else System.out.println("Erro de sintaxe: connect <ip> <porto>");
        }
        return "";
    }
    
    public String getLastCommand(){
        return lastCommand;
    }

    /*@Override
    public void printServers() throws RemoteException {
        if (lastCommand.equalsIgnoreCase(LIST)){
            StringBuilder out = new StringBuilder();
            out.append("Nao esta autenticado nos servidores: ");
            
            ArrayList<ServerEntry> serverList = rmiClient.getService().getServerList();
            Iterator sit = serverList.iterator();
            while (sit.hasNext()){
                ServerEntry se = (ServerEntry)sit.next();
                if (!se.existsClient(view.getName()))
                out.append("\n" + se.getName() + "\t" + se.getAddr().getHostAddress() + "\t" + se.getPort());
            }
            updateView(out.toString());
        }
    }*/
    
    public void updateView(String s){
        view.printContent(s);
    }
    
    public RemoteServiceInterface getRmiService(){
        return rmiClient.getService();
    }
}
