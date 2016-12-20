package Client;

import common.FileObject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    
    public String sendFile(String path){
        byte[] chunk = new byte[1024];
        int nbytes;
        
        //OutputStream os;
        
        FileObject fObj;

        try {
            //OutputStream out = socketToServer.getOutputStream();
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
                    System.out.println("Block: "+ ++count +" Writing: "+fObj.getnBytes()+ " bytes");
                    oout.writeObject(fObj);
                    //oout.reset();
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
    
    public void closeSocket(){
        if(socketToServer != null){
            try {
                socketToServer.close();
            } catch (IOException ex) {}
        }
    }
}
