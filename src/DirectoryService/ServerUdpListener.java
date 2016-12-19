package DirectoryService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerUdpListener {
    
    public static final int MAX_SIZE = 1000;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private boolean listening;
    private InetAddress lastConnectedAddr;
    
    public ServerUdpListener(){
        listening = true;
        try{
            int tempPort = 56321;
            socket = new DatagramSocket(tempPort);
            
        }catch(SocketException e){
            System.out.println("Ocorreu um erro ao nível do socket UDP:\n\t"+e);
        }
    }
    
    public InetAddress getCurrentAddr(){
        return lastConnectedAddr;
    }
    
    protected Object handleRequests() throws IOException, ClassNotFoundException
    {
        ObjectInputStream in;
        packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
        socket.receive(packet);
        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
        return in.readObject();
    }
    
    protected <T> void sendResponse(T response) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        T r = response;
        oos.writeObject(r);
        oos.flush();

        packet.setData(baos.toByteArray());
        packet.setLength(baos.size());
        socket.send(packet);
    }
    
    public int getLocalPort(){
        return socket.getLocalPort();
    }
    
    public void stopListening(){
        listening = false;
    }
    
    public boolean isListening(){
        return listening;
    }
    
    public DatagramSocket getSocket(){
        return socket;
    }
}
