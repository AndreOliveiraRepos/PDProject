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
    
    private int threadId;
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
    
    public static void main(String[] args) {
          
        if(args.length != 3){
            System.out.println("Sintaxe: java FileServer serverName dirServerAddress dirServerUdpPort");
            return;
        }
        
        //Liga ao serviço de directoria e vê se não existe mais nenhum servidor com o mesmo nome
            
        try {    
            FileServer fserver = new FileServer(args[0],
                    InetAddress.getByName(args[1]),
                    Integer.parseInt(args[2])
            );
            
            connectedClients.add("auth");
            
            //Inicializar heartbeat/Packets UDP
            hbSender = new HeartbeatSender<ServerHeartbeat>(directoryServerAddr, directoryServerPort);
            hbSender.setDaemon(true);
            hbSender.start();
            
            hbSender.setHeartbeat(
                new ServerHeartbeat(serverSocket.getLocalPort(),name,connectedClients)
            );
            fserver.processRequests();
            //Esperar que a thread termine
            hbSender.join();
              
        } catch (UnknownHostException ex) {
            System.out.println("Destino desconhecido:\n\t"+ex);
        } catch (InterruptedException ex) {
            System.out.println("Erro na thread UDP:\n\t"+ex);
        }   
    }
    
    public void goOffline(){
        online = false;
    }
    
    public void processRequests(){
        Socket socketToClient;
        
        // Verificar se o socket do servidor foi inicializado
        if (serverSocket == null) return;
        
        System.out.println(name +" Online!");
        
        try {
            while(online){
                socketToClient = serverSocket.accept();
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
}
