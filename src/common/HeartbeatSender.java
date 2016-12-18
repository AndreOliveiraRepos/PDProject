package common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class HeartbeatSender<T> extends Thread{
    private T heartbeat;
    private final InetAddress serverAddr;
    private final int serverPort;
    private boolean running;
    
    private DatagramSocket socket;
    private DatagramPacket packet;
    private static final int TIMEOUT_MS = 10*1000;
    private static final int HEARTBEAT_INTERVAL_MS = 5*1000;
    
    private ByteArrayOutputStream bOut;
    private ObjectOutputStream out;
    
    public HeartbeatSender(T hb, InetAddress serverAddr, int serverPort){
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
        this.heartbeat = hb;
        
        running = true;
    }
    
    public HeartbeatSender(InetAddress serverAddr, int serverPort){
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
        
        running = true;
    }
    
    public void terminate(){
        running = false;
    }
    
    public void setHeartbeat(T hb){
        heartbeat = hb;
    }
    
    @Override
    public void run(){
        
        try{
            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT_MS);
            
            while(running){
                bOut = new ByteArrayOutputStream();          
                out = new ObjectOutputStream(bOut);
                out.writeObject(heartbeat);
                out.flush();
            
                packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), 
                    serverAddr, serverPort);
                socket.send(packet);
                Thread.sleep(HEARTBEAT_INTERVAL_MS);
            }
            
        }catch(SocketException e){
            System.out.println("Ocorreu um erro ao nível do socket UDP:\n\t"+e);
        }catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }catch (InterruptedException ex) {
            System.out.println("Erro na thread ao nível do socket UDP:\n\t"+ex);
        }finally{
            if(socket != null){
                socket.close();
            }
        }
    }
}
