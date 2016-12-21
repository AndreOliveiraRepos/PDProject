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
    
    /*public static final String COPY = "CP";
    public static final String REGISTER = "REGISTER";
    public static final String LOGIN = "LOGIN";
    public static final String LOGOUT = "LOGOUT";
    public static final String MOVE = "MV";
    public static final String CHANGEDIR = "CD";
    public static final String BACKDIR = "CD..";
    public static final String GETCONTENTDIR = "LS";
    public static final String GETFILECONTENT = "CAT";
    public static final String MKDIR = "MKDIR";
    public static final String RMFILE = "RM";*/
    
    public static boolean listenning;
    
    Socket socketToClient;
    int myId;
    String serverName;
    FileSystem serverFileSystem;
    ServerCommands serverCommands;

    public AtendeCliente(Socket s, int id, String name, FileSystem fs){
        socketToClient = s;
        myId = id;
        serverName = name;
        serverFileSystem = fs;
        listenning = true;
        serverCommands = new ServerCommands(serverFileSystem,socketToClient);
    }
    
    @Override
    public void run(){
        String resposta = "";
        String convertedPath = "";
        
        while(listenning){
            String clientRequest = (String)this.readData();
            System.out.println("LEU:" + clientRequest);
            this.sendResponse(serverCommands.Process(clientRequest));
            
        }
        try{
             socketToClient.close();
        } catch (IOException ex) {}
    }
    
    public void writeData(Object obj){
        try {
            ObjectOutputStream out = new ObjectOutputStream(socketToClient.getOutputStream());
            out.writeObject(obj);
            out.flush();
        } catch (IOException ex) {
            System.out.println("Data access error:\n\t"+ex);
        }
    }
    
    public Object readData(){
        Object obj = null;
        try {
            ObjectInputStream in = new ObjectInputStream(socketToClient.getInputStream());
            obj = in.readObject();
        } catch (IOException ex) {
            System.out.println("Data access error:\n\t"+ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Data access error:\n\t"+ex);
        }
        return obj;
    } 
    
    public String receiveFile(String path){
        
        FileObject fObj;
        System.out.println("Writing on " + path);
        try {

            ObjectInputStream ois = new ObjectInputStream(socketToClient.getInputStream());
            FileOutputStream fos = new FileOutputStream(Paths.get(path).toString());
            int contador =0;
            int nbytes;
            //nbytes = fin.read(fileChunk);
            while(true){                    
                
                fObj = (FileObject)ois.readObject();
                System.out.println("Recebido o bloco n. " + ++contador + " com " + fObj.getnBytes() + " bytes.");
                if(fObj.isIsEOF())
                    break;
                
                
                fos.write(fObj.getFileChunk(), 0, fObj.getnBytes());
                System.out.println("Acrescentados " + fObj.getnBytes() + " bytes ao ficheiro " + path+ ".");

            }  
            //
            fos.flush();
            fos.close();
            //in.close();
            //fos.close();
            return "Done!";
        
        } catch (FileNotFoundException ex) {
            return "File not Found!";
        } catch (IOException ex) {
            return "Erros writing!";
        } catch (ClassNotFoundException ex) {
            return "Class not Found!";
        } 
    }
    
    public String sendFile(String path){
        byte[] fileChunk = new byte[2048];
        int nbytes;
        File fileToSend = new File(path);
        FileInputStream fin = null;
        
        
        if(fileToSend.exists()){
            try {
                OutputStream out = socketToClient.getOutputStream();
                fin = new FileInputStream(fileToSend.getAbsolutePath());
                while((nbytes = fin.read(fileChunk))>0){                        
                        
                        out.write(fileChunk, 0, nbytes);
                        out.flush();
                                                
                }   
                fin.close();
                return "File sent";
            } catch (FileNotFoundException ex) {
                return "File not found";
            } catch (IOException ex) {
                return "IO exception";
            }
        }
        else{
            return "File not found!";
        }
    }

    public void sendResponse(String resposta){
        try {
            ObjectOutputStream oout = new ObjectOutputStream(socketToClient.getOutputStream());
            oout.writeObject(resposta);
            oout.flush();
            System.out.println("Resposta enviada: " + resposta);
        } catch (IOException ex) {
            System.out.println("Nao foi possivel enviar resposta ao cliente! " + ex);
        }
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
