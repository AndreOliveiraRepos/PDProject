package FileServer;

import common.FileSystem;
import common.Heartbeat;
import common.HeartbeatSender;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;



class AtendeCliente implements Runnable {
    
    public static final int MAX_SIZE = 4000;
    public static final String COPY = "CP";
    public static final String REGISTER = "REGISTER";
    public static final String LOGIN = "LOGIN";
    public static final String LOGOUT = "LOGOUT";
    public static final String MOVE = "MV";
    public static final String CHANGEDIR = "CD";
    public static final String BACKDIR = "CD..";
    public static final String GETCONTENTDIR = "LS";
    public static final String GETFILECONTENT = "PICO";
    public static final String MKDIR = "MKDIR";
    public static final String RMFILE = "RM";
    
    Socket socketToClient;
    int myId;
    String serverName;
    FileSystem serverFileSystem;

    public AtendeCliente(Socket s, int id,String name, FileSystem fs){
        socketToClient = s;
        myId = id;
        serverName = name;
        serverFileSystem = fs;
    }
    
    @Override
    public void run(){
        BufferedReader in;
        OutputStream out;
        
        /*byte[]fileChunk = new byte[MAX_SIZE];
        int nbytes;*/
        
        String clientRequest = null;
        String resposta = null;
        boolean wtrue = true;
        try{
            while(wtrue){
                
                in = new BufferedReader(
                    new InputStreamReader(
                        socketToClient.getInputStream()
                    )
                );
                out = socketToClient.getOutputStream();

                clientRequest = in.readLine();
                System.out.println("Pedido recebido: "+clientRequest);
                String[] cmd = clientRequest.split("\\s");
                //String[] path;
                
                //String convertedPath = cmd[1].replace("remote"+serverName+"/","C:/");
                //System.out.println("replace: " + p);
                
                switch(cmd[0].toUpperCase()){
                    case "HOME":
                        resposta = "remote"+serverName+"/temp/";
                        
                        break;
                    case GETCONTENTDIR:

                        resposta = "Listing for server "+ cmd[1]+ ":\n" + serverFileSystem.getWorkingDirContent();
                        
                        break;
                    case CHANGEDIR:
                        resposta = serverFileSystem.changeWorkingDirectory(cmd[1]);
                        break;
                    case BACKDIR:
                        resposta = serverFileSystem.changeWorkingDirectory(cmd[0]);
                        break;
                    default:
                        break;
                }
                
                ObjectOutputStream oout = new ObjectOutputStream(out);
                oout.writeObject(resposta);
                System.out.println("Resposta enviada");
                
                
                
            }
            socketToClient.close();
            // Streams de entrada e saída via TCP
             
        }catch(IOException e){
            System.out.println("Ocorreu a excepcao de E/S: \n\t" + e);                       
        }
        
        /*try{
             socketToClient.close();
        } catch (IOException ex) {}*/
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
            
            //Inicializar heartbeat/Packets UDP
            hbSender = new HeartbeatSender<ServerHeartbeat>(
                new ServerHeartbeat(serverSocket.getInetAddress(), serverSocket.getLocalPort(),name,connectedClients),
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
                
                //(new AtendeCliente(socketToClient,threadId++,name,serverFileSystem)).start();
                Thread tt = new Thread((new AtendeCliente(socketToClient,threadId++,name,serverFileSystem)));
                tt.setDaemon(true);
                tt.start();
                tt.join();
                
            } catch (IOException ex) {
            } catch (InterruptedException ex) {
                Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, ex);
            }/*finally{
                try {
                    serverSocket.close();
                } catch (IOException e) {}
            }*/
            
        }  
    }
    
    //arraylist para guardar clientes ligados?
}