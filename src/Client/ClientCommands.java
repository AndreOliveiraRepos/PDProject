package Client;

import DirectoryService.RemoteServiceInterface;
import common.Heartbeat;
import common.HeartbeatSender;
import common.Msg;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class ClientCommands /*extends UnicastRemoteObject implements ServerMonitorListener*/{
    
    private static final String NAME = "NAME";
    private static final String LIST = "LIST";
    private static final String MSG = "MSG";
    private static final String USERS = "USERS";
    private static final String MSGTO = "MSGTO";
    //commands
    public static final String CONNECT = "CONNECT";
    public static final String COPY = "CP";
    public static final String REGISTER = "REGISTER";
    public static final String LOGIN = "LOGIN";
    public static final String LOGOUT = "LOGOUT";
    public static final String MOVE = "MV";
    public static final String CHANGEDIR = "CD";
    public static final String BACKDIR = "CD..";
    public static final String GETCONTENTDIR = "LS";
    public static final String GETFILECONTENT = "CAT";
    public static final String MKDIR = "MKDIR";
    public static final String RMFILE = "RM";
    
    private ClientTcpHandler tcpHandler;
    private ClientUdpHandler udpHandler;
    private static ClientUdpListener udpListener;
    private static HeartbeatSender<Heartbeat> hbSender;
    
    private ClientICommands clientCommands;
    
    private RMIClient rmiClient;
    private static String clientName;
    
    private String lastCommand;
    private Client view;
    private InetAddress directoryServerAddr;
    private Integer directoryServerPort;
    
    private static FileSystemClient clientFileSystem;
    
    public ClientCommands(Client v, InetAddress dirServerAddr, Integer dirServerPort) throws RemoteException
    {
        this.directoryServerAddr = dirServerAddr;
        this.directoryServerPort = dirServerPort;
        udpHandler = new ClientUdpHandler(directoryServerAddr, directoryServerPort);
        udpListener = new ClientUdpListener(this);
        udpListener.start();
        
        tcpHandler = new ClientTcpHandler();
        clientFileSystem = new FileSystemClient(clientName);
        clientCommands = new ClientICommands(clientFileSystem,tcpHandler);
        
        this.clientName = "guest";
        this.view = v;
        this.lastCommand = "";
        
        tcpHandler = new ClientTcpHandler();
        
        // Enviar heartbeats UDP ao serviço de directoria
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
        
        String[] args = msg.split("\\s");
        if (args[0].equalsIgnoreCase(NAME)){
            changeName(args); return "";
        } else if (args[0].equalsIgnoreCase("HELP"))
            return commandList();
        else if (isUdpCommand(args[0]))
            return udpHandler.sendRequest(new Msg(clientName,msg));
        else if (isTcpCommand(args[0])){
            if (args[0].equalsIgnoreCase(CONNECT)){
                connectToTCPServer(args);
            } else {
                tcpHandler.writeData(msg);
                return (String) tcpHandler.readData();
            }
        } else return "Comando desconhecido! Digite: help";
        return "-";
    }
    
    public String processCommand(String msg){
        String[] cmd = msg.split("\\s");
        
        return "";
    }
    
    
    public ClientICommands getClientCommand(){
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
    
    public void changeName(String[] args){
        if (args.length == 2){
            clientName = args[1];
            hbSender.setHeartbeat(new Heartbeat(udpListener.getListeningPort(),clientName));
            System.out.println("Nome: " + clientName);
        } else reportError("Erro de sintaxe: nome <nome>");
    }
    
    public String connectToTCPServer(String[] args){
        if (args.length == 3){
            try {
                tcpHandler.connectToServer(InetAddress.getByName(args[1]), Integer.parseInt(args[2]));
                String m = "HOME "+clientName;
                tcpHandler.writeData("HOME "+clientName);
                clientFileSystem.setRemoteWorkingDir((String) tcpHandler.readData());
                return "Working on "+clientFileSystem.getRemoteWorkingDir();
            } catch (UnknownHostException ex) {
                reportError("Erro: Servidor TCP desconhecido! " + ex);
            }
        } else reportError("Erro de sintaxe: connect <ip> <porto>");
        return "";
    }
    
    public boolean isUdpCommand(String cmd){
        return cmd.equalsIgnoreCase(LIST)
            || cmd.equalsIgnoreCase(MSG)
            || cmd.equalsIgnoreCase(USERS)
            || cmd.equalsIgnoreCase(MSGTO);   
    }
    
    public boolean isTcpCommand(String cmd){
        return cmd.equalsIgnoreCase(CONNECT)
            || cmd.equalsIgnoreCase(COPY)
            || cmd.equalsIgnoreCase(REGISTER)
            || cmd.equalsIgnoreCase(LOGIN)
            || cmd.equalsIgnoreCase(LOGOUT)
            || cmd.equalsIgnoreCase(MOVE)
            || cmd.equalsIgnoreCase(CHANGEDIR)
            || cmd.equalsIgnoreCase(BACKDIR)
            || cmd.equalsIgnoreCase(GETCONTENTDIR)
            || cmd.equalsIgnoreCase(GETFILECONTENT)
            || cmd.equalsIgnoreCase(MKDIR)
            || cmd.equalsIgnoreCase(RMFILE);
    }
    
    public String commandList(){
        return NAME + "\n"
            + LIST 
            + MSG + "\n"
            + USERS + "\n"
            + MSGTO + "\n"
            + CONNECT + "\n"
            + COPY + "\n"
            + REGISTER + "\n"
            + LOGIN + "\n"
            + LOGOUT + "\n"
            + MOVE + "\n"
            + CHANGEDIR + "\n"
            + BACKDIR + "\n"
            + GETCONTENTDIR + "\n"
            + GETFILECONTENT + "\n"
            + MKDIR + "\n"
            + RMFILE + "\n";
    }
}
