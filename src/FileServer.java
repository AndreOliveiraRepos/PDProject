import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class FileServer {
    
    private final String name;
    private final InetAddress directoryServerAddr;
    private final int directoryServerPort;
    
    private ServerSocket serverSocket;
    private static HeartbeatSender hbSender;
    
    public FileServer(String n, InetAddress dirAddr, int dirPort){
        this.name = n;
        this.directoryServerAddr = dirAddr;
        this.directoryServerPort = dirPort;
        
        try {
            //Gera porto automático TCP
            serverSocket = new ServerSocket(0);
        } catch (IOException ex) {
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t"+ex);
        }
    }
    
    public static void main(String[] args) {
          
        if(args.length != 3){
            System.out.println("Sintaxe: java FileServer serverName dirServerAddress dirServerUdpPort");
            return;
        }
            
        try {    
            FileServer fserver = new FileServer(args[0],
                    InetAddress.getByName(args[1]),
                    Integer.parseInt(args[2])
            );
            
            //Inicializar heartbeat/Packets UDP
            fserver.beginHeartbeat();
            //Esperar que a thread termine
            hbSender.join();
            
        } catch (UnknownHostException ex) {
            System.out.println("Destino desconhecido:\n\t"+ex);
        } catch (InterruptedException ex) {
            System.out.println("Erro na thread UDP:\n\t"+ex);
        }
        
    }
    
    public void beginHeartbeat(){
        //Thread que fica encarregada de enviar o heartbeat de 30 em 30 segs
        hbSender = new HeartbeatSender(
                new Heartbeat(serverSocket.getLocalPort(),this.name),
                directoryServerAddr, directoryServerPort
        );
        hbSender.start();
    }
    
    //arraylist para guardar clientes ligados?
}
