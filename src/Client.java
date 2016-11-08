
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class Client {
    //implementar a classe responsável pelo envio do heartbeat ao serviço de directoria
    //socket tcp
    
    private final InetAddress directoryServerAddr;
    private final int directoryServerPort;
    private String name;
    
    private static DatagramSocket socket; //Socket UDP
    private static HeartbeatSender hbSender;
    
    public Client(InetAddress dirAddr, int dirPort){
        this.directoryServerAddr = dirAddr;
        this.directoryServerPort = dirPort;
        
        this.name = "guest";
    }
    
    public static void main(String[] args) {
        
        if(args.length != 2){
            System.out.println("Sintaxe: java Client dirAdress dirUdpPort");
            return;
        }
        
        try {
            Client client = new Client(
                    InetAddress.getByName(args[0]),
                    Integer.parseInt(args[1])
            );
            
            //Socket UDP para receber datagramas
            socket = new DatagramSocket(); 
            
            //Inicializar heartbeat/Packets UDP
            client.beginHeartbeat();
            
            //Esperar que a thread termine
            hbSender.join();
            
        } catch (UnknownHostException ex) {
            System.out.println("[Cliente] Destino desconhecido:\n\t"+ex);
        } catch (InterruptedException ex) {
            System.out.println("[Cliente-Heartbeat] Erro na thread UDP de cliente:\n\t"+ex);
        } catch (SocketException ex) {
            System.out.println("[Cliente] Ocorreu um erro ao nível do socket UDP:\n\t"+ex);
        }
    }
    
    public void beginHeartbeat(){
        //Thread que fica encarregada de enviar o heartbeat de 30 em 30 segs
        hbSender = new HeartbeatSender(
                new Heartbeat(socket.getLocalPort(),this.name),
                directoryServerAddr, directoryServerPort
        );
        hbSender.start();
    }
}
