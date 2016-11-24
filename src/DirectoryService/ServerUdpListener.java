package DirectoryService;

import common.Heartbeat;
import common.Msg;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ServerUdpListener extends Thread{
    public static final int MAX_SIZE = 1000;
    public static final String LIST = "LIST";
    public static final String MSG = "MSG";
    protected DatagramSocket socket;
    protected boolean listening;
    
    public ServerUdpListener(){
        listening = true;
    }
    
    @Override
    public void run(){ 
        try{
            int tempPort = 56321;
            socket = new DatagramSocket(tempPort);
            DatagramPacket packet = null;
            System.out.println("UDP Port:\n\t"+socket.getLocalPort()+" v3");
            
            while(listening){
                handleRequests(packet);
            } 
            
        }catch(UnknownHostException e){
             System.out.println("Destino desconhecido:\n\t"+e);
        }catch(SocketTimeoutException e){
            System.out.println("Não foi recebida qualquer resposta:\n\t"+e);
        }catch(SocketException e){
            System.out.println("Ocorreu um erro ao nível do socket UDP:\n\t"+e);
        }catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+e);
        }catch(ClassNotFoundException e){
             System.out.println("O objecto recebido não é do tipo esperado:\n\t"+e);
        }finally{
            if(socket != null){
                socket.close();
            }
        }
    }
    
    protected void handleRequests(DatagramPacket packet) throws IOException, ClassNotFoundException
    {
        ObjectInputStream in;
        Object obj;
        packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
        socket.receive(packet);
        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
        obj = in.readObject();
        System.out.println("packet received...");

        if (obj instanceof Heartbeat){
            Heartbeat heartbeat = (Heartbeat)obj;
            System.out.println("Heatbeat data: \n\t TCPServerName: "+heartbeat.getName()
                                + "\n\t TCPServerPort: "+heartbeat.getPort());
        } else if (obj instanceof Msg){
            Msg msg = (Msg)obj;
            
            if(msg.getMsg().equalsIgnoreCase(LIST)){
                sendConnectedServers(packet);
            } else /*if (msg.getMsg().equalsIgnoreCase(MSG))*/{
                System.out.println("Message: \n\t Nickname: "+msg.getName()
                                + "\n\t Text: "+msg.getMsg());
                sendResponse(packet);
            } 
        } else if (obj instanceof String){
            System.out.println((String)obj);
        } else
            System.out.println("Erro: Objecto recebido do tipo inesperado!");
    }
    
    protected void sendConnectedServers(DatagramPacket packet) throws IOException
    {
        System.out.println("vou enviar listagem de servidores");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        String r = "Objecto com listagem de servers activos\n";
        oos.writeObject(r);
        oos.flush();

        packet.setData(baos.toByteArray());
        packet.setLength(baos.size());
        socket.send(packet);
        System.out.println("enviei");
    }
    
    protected void sendResponse(DatagramPacket packet) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        String r = "Servidor recebeu o packet UDP";
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
}
