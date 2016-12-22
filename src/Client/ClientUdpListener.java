package Client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ClientUdpListener extends Thread
{    
    private static final int MAX_SIZE = 512;
    
    private DatagramSocket socket;
    private DatagramPacket packet;
    
    private Client controller;
    
    private boolean listening;
    private String output;
    
    public ClientUdpListener(Client c){
        controller = c;
        
        try {
            socket = new DatagramSocket();
        } catch (SocketException ex) {
            System.out.println("Nao foi possivel criar o socket UDP para escuta! " + ex);
        }
        
        listening = true;
    }
    
    @Override
    public void run()
    {
        ObjectInputStream in;
        Object obj;
        
        try 
        {
            while (listening) {
                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                socket.receive(packet);

                in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                obj = in.readObject();

                if (obj instanceof String){
                    //System.out.println((String)obj);
                    output = (String)obj;
                } else dispatch("Objecto recebido no socket UDP do tipo inesperado! ");
            }
        } catch (IOException ex) {
            System.out.println("Erro ao receber dados no socket UDP de escuta! " + ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Objecto recebido no socket UDP do tipo inesperado! " + ex);
        }
    }
    
    public InetAddress getLocalAddr(){
        return socket.getLocalAddress();
    }
    
    public int getListeningPort(){
        return socket.getLocalPort();
    }
    
    public void closeSocket(){
        socket.close();
    }
    
    public void stopListening(){
        listening = false;
    }
    
    public void dispatch(String s){
        controller.updateView(s);
    }
}