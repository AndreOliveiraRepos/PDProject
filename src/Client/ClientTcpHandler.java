package Client;

import common.FileObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientTcpHandler {
    public static final int TIMEOUT = 5000; //5 segs
    
    protected InetAddress serverAddress;
    protected int serverPort;
    protected Socket socketToServer;
    private boolean online;
    
    public ClientTcpHandler()
    {
        socketToServer = null;
    }
    
    public boolean connectToServer(InetAddress servAddr, Integer servPort){
        try {
            if(socketToServer != null){
                socketToServer.close();
            }
            socketToServer = new Socket(servAddr, servPort);
            //socketToServer.setSoTimeout(TIMEOUT);
            online = true;
            return true;
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket TCP" + ":\n\t"+e);
            return false;
        }
    }
    
    public Object readData(){
        Object obj = null;
        try {
            ObjectInputStream in = new ObjectInputStream(socketToServer.getInputStream());
            obj = in.readObject();
        } catch (IOException ex) {
            System.out.println("Data access error:\n\t"+ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Data access error:\n\t"+ex);
        }
        return obj;
    } 
    
    public void writeData(Object obj){
        try {
            ObjectOutputStream out = new ObjectOutputStream(socketToServer.getOutputStream());
            out.writeObject(obj);
            out.flush();
        } catch (IOException ex) {
            System.out.println("Data access error:\n\t"+ex);
        }
    }
    
    public  String sendFile(String path){
        byte[] chunk = new byte[1024];
        int nbytes;
        FileObject fObj;
        try {
            ObjectOutputStream oout = new ObjectOutputStream(socketToServer.getOutputStream());
            FileInputStream fis = new FileInputStream(path);
            int count = 0;
            nbytes = fis.read(chunk);
            while(true){   
                    if(nbytes == -1)
                        break;
                    fObj = new FileObject();
                    fObj.setFileChunk(chunk);
                    fObj.setnBytes(nbytes);
                    //System.out.println("Block: "+ ++count +" Writing: "+fObj.getnBytes()+ " bytes");
                    oout.writeObject(fObj);
                    oout.flush();
                    nbytes = fis.read(chunk);

            }  
            fObj = new FileObject();
            fObj.setIsEOF(true);
            oout.writeObject(fObj);
            oout.flush();

            fis.close();
            
            return "File sent to server";
        } catch (FileNotFoundException ex) {
            return "File not found";
        } catch (IOException ex) {
            return "IO exception";
        }
    }
    
    public  String receiveFile(String path){
        File fileToWrite = new File(path);
        System.out.println("CAMINHO:" + path);
        FileObject fObj;
        if(fileToWrite.exists()){
            return "File already exists";
        }else{
            System.out.println("Writing on " + path);
            try {

                ObjectInputStream ois = new ObjectInputStream(socketToServer.getInputStream());
                FileOutputStream fos = new FileOutputStream(fileToWrite);
                int contador =0;
                int nbytes;
                //nbytes = fin.read(fileChunk);
                while(true){                    
                    Object obj = ois.readObject();
                    // 
                    //System.out.println("Recebido o bloco n. " + ++contador + " com " + fObj.getnBytes() + " bytes.");
                    if(obj instanceof String){
                        return (String)obj;
                    }else{
                        fObj = (FileObject)obj;
                        if(fObj.isIsEOF())
                            break;
                        fos.write(fObj.getFileChunk(), 0, fObj.getnBytes());
                    }
                    


                    
                    //System.out.println("Acrescentados " + fObj.getnBytes() + " bytes ao ficheiro " + path+ ".");

                }  
                //
                fos.flush();
                fos.close();
                //in.close();
                //fos.close();
                return "Received from server!";

            } catch (FileNotFoundException ex) {
                return "File not Found!";
            } catch (IOException ex) {
                return "Erros writing!";
            } catch (ClassNotFoundException ex) {
                return "Class not Found!";
            } 
        }
        
    }
    public void closeSocket(){
        if(socketToServer != null){
            try {
                socketToServer.close();
            } catch (IOException ex) {}
        }
    }
    
    public boolean isOnline(){
        return this.online;
    }
}
