package Client;

import DirectoryService.RemoteServiceInterface;
import common.FileSystem;
import common.Heartbeat;
import common.HeartbeatSender;
import common.Msg;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class Client /*extends UnicastRemoteObject implements ServerMonitorListener*/{
 
    private ClientTcpHandler tcpHandler;
    private ClientUdpHandler udpHandler;
    private static ClientUdpListener udpListener;
    private static HeartbeatSender<Heartbeat> hbSender;
    private ClientCommands clientCommands;
    private RMIClient rmiClient;
    private static String clientName;
    private String lastCommand;
    private ClientUI view;
    private InetAddress directoryServerAddr;
    private Integer directoryServerPort;
    private String remoteWorkingDir;
    private boolean online;
    
    private static FileSystem clientFileSystem;
    
    public Client(ClientUI v, InetAddress dirServerAddr, Integer dirServerPort) throws RemoteException
    {
        this.directoryServerAddr = dirServerAddr;
        this.directoryServerPort = dirServerPort;
        udpHandler = new ClientUdpHandler(directoryServerAddr, directoryServerPort);
        //isto pra metodo
        udpListener = new ClientUdpListener(this);
        udpListener.start();
        
        tcpHandler = new ClientTcpHandler();
        clientFileSystem = new FileSystem(clientName);
        
        
        this.clientName = "guest";
        this.view = v;
        this.lastCommand = "";
        this.remoteWorkingDir = "";
        
       // tcpHandler = new ClientTcpHandler();
        //hbSender = null;
        clientCommands = new ClientCommands(clientFileSystem,tcpHandler,this);
        
    }
    
    public void goOnline(){
        this.startHearbeat();
        this.online = true;
    }
    
    public void startHearbeat(){
        hbSender = new HeartbeatSender<Heartbeat>(
                new Heartbeat(udpListener.getListeningPort(),clientName),
                directoryServerAddr, directoryServerPort);
        hbSender.setDaemon(true);
        hbSender.start();
    }
    
    public String processRequest(String msg) throws UnknownHostException, IOException{
        lastCommand = msg;
        
        if (rmiClient == null){
            System.out.println("Servico rmi iniciado! ");
            this.rmiClient = new RMIClient(directoryServerAddr.getHostAddress() ,directoryServerPort, this);
            rmiClient.run();
        }
        clientCommands.Process(msg);
        
        return "-";
    }
    
    
    public ClientCommands getClientCommand(){
        return this.clientCommands;
    }
    
    public String getLastCommand(){
        return lastCommand;
    }
    
    public void updateView(String s){
        view.printContent(s);
    }
    
    public void reportError(String e){
        view.printError(e);
    }
    
    public RemoteServiceInterface getRmiService(){
        return rmiClient.getService();
    }
    
    public void terminate(){
        udpHandler.closeSocket();
        tcpHandler.closeSocket();
    }
    
    /*public void changeName(String[] args){
        if (args.length == 2){
            clientName = args[1];
            hbSender.setHeartbeat(new Heartbeat(udpListener.getListeningPort(),clientName));
            System.out.println("Nome: " + clientName);
        } else reportError("Erro de sintaxe: nome <nome>");
    }*/
    
    /*public String connectToTCPServer(String[] args){
        if (args.length == 3){
            try {
                tcpHandler.connectToServer(InetAddress.getByName(args[1]), Integer.parseInt(args[2]));
                String m = "HOME "+clientName;
                tcpHandler.writeData("HOME "+clientName);
                //clientFileSystem.setRemoteWorkingDir((String) tcpHandler.readData());
                //return "Working on "+clientFileSystem.getRemoteWorkingDir();
            } catch (UnknownHostException ex) {
                reportError("Erro: Servidor TCP desconhecido! " + ex);
            }
        } else reportError("Erro de sintaxe: connect <ip> <porto>");
        return "";
    }*/

    public static String getClientName() {
        return clientName;
    }

    public static void setClientName(String clientName) {
        Client.clientName = clientName;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public static HeartbeatSender<Heartbeat> getHbSender() {
        return hbSender;
    }

    public static void setHbSender(HeartbeatSender<Heartbeat> hbSender) {
        Client.hbSender = hbSender;
    }

    public ClientTcpHandler getTcpHandler() {
        return tcpHandler;
    }

    public void setTcpHandler(ClientTcpHandler tcpHandler) {
        this.tcpHandler = tcpHandler;
    }

    public ClientUdpHandler getUdpHandler() {
        return udpHandler;
    }

    public void setUdpHandler(ClientUdpHandler udpHandler) {
        this.udpHandler = udpHandler;
    }

    public static ClientUdpListener getUdpListener() {
        return udpListener;
    }

    public static void setUdpListener(ClientUdpListener udpListener) {
        Client.udpListener = udpListener;
    }
    
    public void setRemoteDir(String path){
        this.remoteWorkingDir = path;
    }
    
    
}
