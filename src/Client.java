
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Client {
    public static final int MAX_SIZE = 4000;
    public static final int TIMEOUT = 5;
    
    private final InetAddress directoryServerAddr;
    private final int directoryServerPort;
    private String name;
    
    private static Socket socketToServer; //Socket TCP
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
            
            //Socket UDP para receber datagramas (usado para chat) ?
            socket = new DatagramSocket(); 
            
            //Inicializar heartbeat/Packets UDP
            client.beginHeartbeat();
            //teste TCP
            client.connectToServer(InetAddress.getByName("127.0.0.1"), 7001);
            
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
    
    public void connectToServer(InetAddress servAddr, int servPort){
        
        PrintWriter pout;
        InputStream in;
        
        //int nbytes;
        //byte []fileChunck = new byte[MAX_SIZE];
        try {
            socketToServer = new Socket(servAddr, servPort);
            socketToServer.setSoTimeout(TIMEOUT*1000);
            
            in = socketToServer.getInputStream();
            pout = new PrintWriter(socketToServer.getOutputStream(), true);
            
            // Enviar um pedido ao servidor de ficheiros (TCP)
            pout.println("meuRequest1");
            pout.flush();

            // Receber a resposta do servidor (TCP)
            
            //DEBUG - ver se o tcp está funcional
            String resposta;
            BufferedReader in_ = new BufferedReader(new InputStreamReader(in));
            
            resposta = in_.readLine();  
            System.out.println(resposta);
            // fim Debug
            
        } catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket" + ":\n\t"+e);
        }finally{
            
            if(socketToServer != null){
                try {
                    socketToServer.close();
                } catch (IOException ex) {}
            } 
        }     
    }
}
