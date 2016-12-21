package Client;

import DirectoryService.ServerEntry;
import common.Msg;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class UDPClient {
    private static final int TIMEOUT = 5000;
    
    InetAddress directoryServerAddr;
    Integer directoryServerPort;
    private DatagramSocket socket;
    
    public UDPClient(InetAddress dirServerAddr, Integer dirServerPort){
        
        directoryServerAddr = dirServerAddr;
        directoryServerPort = dirServerPort;
        
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT);
            
        } catch (SocketException ex) {
            System.out.println("[Client UDP] Erro: Nao foi possivel iniciar o socket UDP! " + ex);
        }
    }
    
    public void sendRequest(Msg msg)
    {
        try {
            if (socket == null) return;
            
            ByteArrayOutputStream baos;
            ObjectOutputStream oOut;
            baos = new ByteArrayOutputStream();
            oOut = new ObjectOutputStream(baos);
            oOut.writeObject(msg);
            oOut.flush();
            
            DatagramPacket packet = new DatagramPacket(baos.toByteArray(), baos.size(),
                    directoryServerAddr, directoryServerPort);
            socket.send(packet);
            
        } catch (IOException ex) {
            System.out.println("Erro ao enviar o pedido UDP ao servico de directoria!" + ex);
        }
    }
}
