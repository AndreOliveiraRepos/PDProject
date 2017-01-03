package Client;

import DirectoryService.Manager.ClientEntry;
import DirectoryService.Manager.ServerEntry;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class ClientUdpListener extends Thread
{    
    private static final int MAX_SIZE = 512;
    
    private DatagramSocket socket;
    private DatagramPacket packet;
    
    private ClientCommands controller;
    
    private boolean listening;
    
    //Received data
    private String output;
    ArrayList<ServerEntry> servers;
    ArrayList<ClientEntry> clients;
    
    public ClientUdpListener(ClientCommands c){
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

                /*if (obj instanceof String){
                    //System.out.println("Recebi: " + (String)obj);
                    output = (String)obj;
                    updateServerList();
                } else dispatch("Objecto recebido no socket UDP do tipo inesperado! ");*/
                try
                {
                    if(obj instanceof ArrayList<?>)
                    {
                        if(((ArrayList<?>)obj).get(0) instanceof ServerEntry)
                        {
                            servers = (ArrayList<ServerEntry>) obj;
                            updateServerList();
                        }
                        else if(((ArrayList<?>)obj).get(0) instanceof ClientEntry)
                        {
                            clients = (ArrayList<ClientEntry>) obj;
                            updateUserList();
                        } else System.out.println("Erro: ArrayList de objectos nao conhecidos!");
                    }
                }
                catch(NullPointerException e)
                {
                    System.out.println("Erro ao receber objecto no listener UDP!" + e);
                }
            }
        } catch (SocketException ex){
            System.out.println("Socket UDP de escuta desligado! ");
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
    
    public void terminate(){
        closeSocket();
        stopListening();
    }
    
    public void dispatch(String s){
        controller.updateView(s);
    }
    
    public void updateServerList(){
        StringBuilder os = new StringBuilder();
        os.append("Lista de servidores ligados: \n");
        for (ServerEntry s : servers){
            os.append("\t" + s.toString() + "\n");
        }
        controller.updateServerList(os.toString());
    }
    
    public void updateUserList(){
        StringBuilder os = new StringBuilder();
        os.append("Lista de clientes ligados: \n");
        for (ClientEntry c : clients){
            os.append(c.toString() + "\n");
        }
        controller.updateUserList(os.toString());
    }
}