import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class HeartbeatSender extends Thread{
    private final Heartbeat heartbeat;
    private final InetAddress serverAddr;
    private final int serverPort;
    private boolean running;
    
    private DatagramSocket socket;
    private DatagramPacket packet;
    private static final int TIMEOUT_MS = 10*1000;
    private static final int HEARTBEAT_INTERVAL_MS = 5*1000;
    
    private ByteArrayOutputStream bOut;
    private ObjectOutputStream out;
    
    public HeartbeatSender(Heartbeat hb, InetAddress serverAddr, int serverPort){
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
        this.heartbeat = hb;
        
        running = true;
    }
    
    public void terminate(){
        running = false;
    }
    
    @Override
    public void run(){
        
        try{
            
            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT_MS);
            
            bOut = new ByteArrayOutputStream();          
            out = new ObjectOutputStream(bOut);
            
            out.writeObject(heartbeat);
            out.flush(); out.close();
            
            packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), 
                    serverAddr, serverPort);
            
            while(running){
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
