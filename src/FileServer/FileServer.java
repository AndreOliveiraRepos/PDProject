package FileServer;

import common.FileObject;
import common.FileSystem;
import common.HeartbeatSender;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;



class AtendeCliente extends Thread {
    
    public static final int MAX_SIZE = 4000;
    
    
   
    
    public static boolean listenning;
    
    //Socket socketToClient;
    int myId;
    String serverName;
    FileSystem serverFileSystem;
    ServerCommands serverCommands;
    ServerTCPHandler tcpHandler;

    public AtendeCliente(Socket s, int id, String name, FileSystem fs){
        tcpHandler = new ServerTCPHandler();
        tcpHandler.setSocket(s);
        myId = id;
        serverName = name;
        serverFileSystem = fs;
        listenning = true;
        serverCommands = new ServerCommands(serverFileSystem,tcpHandler);
    }
    
    @Override
    public void run(){
        String resposta = "";
        String convertedPath = "";
        
        while(listenning){
            String clientRequest = (String)this.tcpHandler.readData();
            System.out.println("LEU:" + clientRequest);
            this.tcpHandler.writeData(serverCommands.Process(clientRequest));
            
        }
        tcpHandler.closeSocket();
    }
    
    
}

public class FileServer {
    
    private static String name;
    private static InetAddress directoryServerAddr;
    private static int directoryServerPort;
    
    private static HeartbeatSender<ServerHeartbeat> hbSender;
    private static boolean online;
    
    private static int threadId;
    private static ServerSocket serverSocket;  //TCP Server
    
    private static ArrayList<String> connectedClients;
    private static FileSystem serverFileSystem;
    
    //directory
    
    public FileServer(String n, InetAddress dirAddr, int dirPort) {
        
        directoryServerAddr = dirAddr;
        directoryServerPort = dirPort;
        threadId = 0;
        name = n;
        connectedClients = new ArrayList<String>();
        online = true;
        serverFileSystem = new FileSystem(name);
        
        try {
            //Gera porto automático TCP
            serverSocket = new ServerSocket(7001); //0 //7001
        } catch (IOException ex) {
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+ex);
        }
    }

    public static String getName() {
        return name;
    }

    public static InetAddress getDirectoryServerAddr() {
        return directoryServerAddr;
    }

    public static int getDirectoryServerPort() {
        return directoryServerPort;
    }

    public static HeartbeatSender<ServerHeartbeat> getHbSender() {
        return hbSender;
    }

    public static boolean isOnline() {
        return online;
    }

    public int getThreadId() {
        return threadId;
    }

    public ArrayList<String> getConnectedClients(){ return this.connectedClients;}
    
    public ServerSocket getServerSocket(){ return this.serverSocket;}
    
    public FileSystem getServerFileSystem(){ return this.serverFileSystem;}
    
    public void goOffline(){
        online = false;
    }
    
    public void goOnline(){
        this.startHearbeat();
        
        
        
    }
    
    public void processRequests(){
        Socket socketToClient;
        System.out.println("ESTADO: " + this.online);
        // Verificar se o socket do servidor foi inicializado
        if (serverSocket == null) return;
        
        System.out.println(name +" Online!");
        
        try {
            while(online){
                socketToClient = serverSocket.accept();
                //System.out.println("ACEITEI");
                (new AtendeCliente(socketToClient, threadId++, name, serverFileSystem)).start();
            }
        } catch (IOException ex) {
            System.out.println("Ocorreu um erro ao criar o socket TCP! " + ex);
        } finally{
            try {
                serverSocket.close();
            } catch (IOException e) {}
        }
    }

    public static void setName(String name) {
        FileServer.name = name;
    }

    public static void setDirectoryServerAddr(InetAddress directoryServerAddr) {
        FileServer.directoryServerAddr = directoryServerAddr;
    }

    public static void setDirectoryServerPort(int directoryServerPort) {
        FileServer.directoryServerPort = directoryServerPort;
    }

    public static void setHbSender(HeartbeatSender<ServerHeartbeat> hbSender) {
        FileServer.hbSender = hbSender;
    }

    public static void setOnline(boolean online) {
        FileServer.online = online;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public static void setServerSocket(ServerSocket serverSocket) {
        FileServer.serverSocket = serverSocket;
    }

    public static void setConnectedClients(ArrayList<String> connectedClients) {
        FileServer.connectedClients = connectedClients;
    }

    public static void setServerFileSystem(FileSystem serverFileSystem) {
        FileServer.serverFileSystem = serverFileSystem;
    }
    
    public void startHearbeat(){
        try {
            this.online = true;
            hbSender = new HeartbeatSender<ServerHeartbeat>(directoryServerAddr, directoryServerPort);
            hbSender.setDaemon(true);
            hbSender.start();

            hbSender.setHeartbeat(
                new ServerHeartbeat(serverSocket.getLocalPort(),name,connectedClients)
            );
            processRequests();
           
        
            //Esperar que a thread termine
            hbSender.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
}
