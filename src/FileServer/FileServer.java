package FileServer;

import common.Heartbeat;
import common.HeartbeatSender;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

class AtendeCliente extends Thread {
    
    public static final int MAX_SIZE = 4000;
    
    Socket socketToClient;
    int myId;

    public AtendeCliente(Socket s, int id){
        socketToClient = s;
        myId = id;
    }
    
    @Override
    public void run(){
        BufferedReader in;
        OutputStream out;
        
        /*byte[]fileChunk = new byte[MAX_SIZE];
        int nbytes;*/
        
        String clientRequest = null;
        String resposta = null;
        
        try{            
            // Streams de entrada e saída via TCP
            in = new BufferedReader(
                    new InputStreamReader(
                            socketToClient.getInputStream()
                    )
            );
            out = socketToClient.getOutputStream();
            
            clientRequest = in.readLine();
            System.out.println("Pedido recebido: "+clientRequest);
            
            // Enviar resposta ao cliente
            resposta = "O servidor recebeu o pedido" + clientRequest;
            out.write(resposta.getBytes(),0,resposta.length());
            out.flush();
            
        }catch(IOException e){
            System.out.println("Ocorreu a excepcao de E/S: \n\t" + e);                       
        }
        
        try{
             socketToClient.close();
        } catch (IOException ex) {}
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
    //directory
    
    public FileServer(String n, InetAddress dirAddr, int dirPort) {
        
        directoryServerAddr = dirAddr;
        directoryServerPort = dirPort;
        threadId = 0;
        name = n;
        connectedClients = new ArrayList<String>();
        online = true;
        
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
        Socket socketToClient;
        
        // Verificar se o socket do servidor foi inicializado
        if (serverSocket == null) return;
        
        System.out.println(name +" Online!");
        
        while(online){
            try {
                socketToClient = serverSocket.accept();
                (new AtendeCliente(socketToClient,threadId++)).start();
            } catch (IOException ex) {
            } finally{
                try {
                    serverSocket.close();
                } catch (IOException e) {}
            }
        }  
    }
    
    //arraylist para guardar clientes ligados?
}
