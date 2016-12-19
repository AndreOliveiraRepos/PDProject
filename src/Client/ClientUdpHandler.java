package Client;

import DirectoryService.ServerEntry;
import common.Msg;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ClientUdpHandler{
    public static final int MAX_SIZE = 1000;
    protected DatagramSocket socket;
    protected DatagramPacket packet;
    
    InetAddress directoryServerAddr;
    Integer directoryServerPort;
    ArrayList<ServerEntry> availableServers;
    
    public ClientUdpHandler(InetAddress dirServerAddr, Integer dirServerPort){
        try 
        {
            socket = new DatagramSocket(); //Não pode ser 6001, deve de ser () ... automático.
            socket.setSoTimeout(3500);
        } catch(SocketException e){
            System.out.println("[Receiver] Ocorreu um erro ao nível do socket UDP:\n\t"+e);
        }
        finally{
            System.out.println("Udp client port:\n\t"+socket.getLocalPort());
        }
        directoryServerAddr = dirServerAddr;
        directoryServerPort = dirServerPort;
        
        availableServers = new ArrayList<ServerEntry>();
    }
    
    public int getLocalPort(){
        return socket.getLocalPort();
    }
    
    public String sendRequest(Msg msg) throws IOException, ClassNotFoundException
    {
        if (socket == null) return null;
        
        ByteArrayOutputStream baos;
        ObjectOutputStream oOut;
        baos = new ByteArrayOutputStream();
        oOut = new ObjectOutputStream(baos);
        oOut.writeObject(msg);
        oOut.flush();

        packet = new DatagramPacket(baos.toByteArray(), baos.size(),
                directoryServerAddr, directoryServerPort);
        socket.send(packet);
        
        return getResponse();
    }
    
    protected String getResponse() throws IOException, ClassNotFoundException{
        ObjectInputStream in;
        Object obj;
        
        packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
        socket.receive(packet);

        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
        obj = in.readObject();

        if (obj instanceof String){
            return (String)obj;
        } else if (obj instanceof ArrayList) {
            availableServers = (ArrayList)obj;
            return getAvailableServers();
        }else {
            System.out.println("Erro: Objecto recebido do tipo inesperado!");
        }
        return "";
    }
    
    public void closeSocket(){
        socket.close();
    }
    
    private String getAvailableServers(){
        
        StringBuilder buff = new StringBuilder();
        Iterator it = availableServers.iterator();
        buff.append("\n");
        while (it.hasNext()) {
            ServerEntry se = (ServerEntry)it.next();
            //buff.append(entry.getKey() + " - " + entry.getValue().getPort() + "\n");
            buff.append(se.getName() + "\t" + se.getAddr() + "\t" + se.getPort() + "\n");
        }
        return buff.toString();
    }
}
