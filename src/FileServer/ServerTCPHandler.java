/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileServer;

import common.FileObject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Paths;

/**
 *
 * @author André Oliveira
 */
public class ServerTCPHandler {
    public static final int TIMEOUT = 5000; //5 segs
    
    
    protected Socket socketToClient;
    private boolean online;
    
    public ServerTCPHandler(){
        this.socketToClient = null;
    }
    
    public void setSocket(Socket s){
        this.socketToClient = s;
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
    
    public void writeData(Object obj){
        try {
            ObjectOutputStream out = new ObjectOutputStream(socketToClient.getOutputStream());
            out.writeObject(obj);
            out.flush();
        } catch (IOException ex) {
            System.out.println("Data access error:\n\t"+ex);
        }
       
    }
    
    public String sendFile(String path){
        byte[] chunk = new byte[1024];
        int nbytes;
        FileObject fObj;
        try {
            ObjectOutputStream oout = new ObjectOutputStream(socketToClient.getOutputStream());
            FileInputStream fis = new FileInputStream(path);
            int count = 0;
            nbytes = fis.read(chunk);
            while(true){   
                    if(nbytes == -1)
                        break;
                    fObj = new FileObject();
                    fObj.setFileChunk(chunk);
                    fObj.setnBytes(nbytes);
                    System.out.println("Block: "+ ++count +" Writing: "+fObj.getnBytes()+ " bytes");
                    oout.writeObject(fObj);
                    oout.flush();
                    nbytes = fis.read(chunk);

            }  
            fObj = new FileObject();
            fObj.setIsEOF(true);
            oout.writeObject(fObj);
            oout.flush();

            fis.close();
            
            return "File sent";
        } catch (FileNotFoundException ex) {
            return "File not found";
        } catch (IOException ex) {
            return "IO exception";
        }
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
    
    public void closeSocket(){
        if(socketToClient != null){
            try {
                socketToClient.close();
            } catch (IOException ex) {}
        }
    }
    
    public boolean isOnline(){
        return this.online;
    }

}
