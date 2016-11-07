
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class UDPReceiver extends Thread{
    public static final int MAX_SIZE = 10000;
    DatagramSocket socket;
    
    public UDPReceiver(){
        try {
            socket = new DatagramSocket(6001); //Não pode ser 6001, deve de ser () ... automático.
        } catch(SocketException e){
            System.out.println("[Receiver] Ocorreu um erro ao nível do socket UDP:\n\t"+e);
        }finally{
            System.out.println("Directory server port:\n\t"+socket.getLocalPort());
        }
    }
    
    @Override
    public void run(){
        DatagramPacket packet = null;
        Heartbeat heartbeat = null;
        
        ObjectInputStream in;
        Object obj;
        
        try{
            
            while(true){
            
                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
                socket.receive(packet);

                in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                obj = in.readObject();
                //heartbeat = (Heartbeat)in.readObject();
                
                System.out.println("packet received...");
                
                if (obj instanceof Heartbeat){
                    heartbeat = (Heartbeat)obj;
                    System.out.println("Heatbeat data: \n\t TCPServerName: "+heartbeat.getName()
                                        + "\n\t TCPServerPort: "+heartbeat.getTCPPort());
                }
                
            }
            
        }catch(UnknownHostException e){
             System.out.println("Destino desconhecido:\n\t"+e);
        }catch(NumberFormatException e){
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
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
}
