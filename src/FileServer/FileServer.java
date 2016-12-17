package FileServer;

import common.HeartbeatSender;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class FileServer {
    
    private static String name;
    private static InetAddress directoryServerAddr;
    private static int directoryServerPort;
    
    private static HeartbeatSender<ServerHeartbeat> hbSender;
    private static boolean online;
    
    private int threadId;
    private static ServerSocket serverSocket;  //TCP Server
    
    private static ArrayList<String> connectedClients;
    
    public FileServer(String n, InetAddress dirAddr, int dirPort) {
        
        directoryServerAddr = dirAddr;
        directoryServerPort = dirPort;
        threadId = 0;
        name = n;
        connectedClients = new ArrayList<String>();
        online = true;
        
        connectedClients.add("lm1");
        connectedClients.add("lm2");
        
        try {
            //Gera porto automático TCP
            serverSocket = new ServerSocket(0); //0 //7001
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
            
            //Inicializar heartbeat/Packets UDP
            hbSender = new HeartbeatSender<ServerHeartbeat>(
                new ServerHeartbeat(serverSocket.getLocalPort(),name,connectedClients),
                directoryServerAddr, directoryServerPort);
            hbSender.setDaemon(true);
            hbSender.start();
            
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
        
        if (serverSocket == null) return;
        
        System.out.println(name +" Online!");
        
        while(online){
            try {
                Socket socketToClient = serverSocket.accept();
                (new ClientRequestHandler(socketToClient,threadId++)).start();
            } catch (IOException ex) {
                System.out.println("Erro ao aceitar um novo cliente TCP! " + ex);
            } finally{
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.out.println("Erro ao fechar o socket TCP do servidor! " + e);
                }
            }
        }  
    }
}
