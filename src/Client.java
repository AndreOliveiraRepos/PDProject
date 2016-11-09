
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    public static final int MAX_SIZE = 4000;
    public static final int TIMEOUT = 5;
    
    private static InetAddress directoryServerAddr;
    private static int directoryServerPort;
    private static String name;
    
    private static Socket socketToServer; //Socket TCP
    private static UdpReceiver udpReceiver;
    private static HeartbeatSender hbSender;
    
    public Client(InetAddress dirAddr, int dirPort){
        directoryServerAddr = dirAddr;
        directoryServerPort = dirPort;
        
        name = "guest";
    }
    
    public static void main(String[] args) {
        
        if(args.length != 2){
            System.out.println("Sintaxe: java Client dirAdress dirUdpPort");
            return;
        }
        
        try {
            // Inicializa o cliente com os args passados por terminal
            Client client = new Client(
                    InetAddress.getByName(args[0]),
                    Integer.parseInt(args[1])
            );
            
            // Fica á escuta no Porto UDP automático
            startUdpListener();
            
            // Enviar heartbeats UDP ao serviço de directoria
            beginHeartbeat();
            
            // Ligar ao servidor de ficheiros TCP
            connectToTcpServer(InetAddress.getByName("127.0.0.1"), 7001);
            
            System.out.println(client.askTcpServer("pedido1"));
            //Esperar que as threads terminem
            //x.join();
            
        } catch (UnknownHostException ex) {
            System.out.println("[Cliente] Destino desconhecido:\n\t"+ex);
        } /*catch (InterruptedException ex) {
            System.out.println("[Cliente-Heartbeat] Erro na thread UDP de cliente:\n\t"+ex);
        }*/
    }
    
    /**
     * Thread que fica encarregada de enviar o heartbeat de 30 em 30 segs.
     */
    public static void beginHeartbeat(){
        hbSender = new HeartbeatSender(
                new Heartbeat(udpReceiver.getLocalPort(),name),
                directoryServerAddr, directoryServerPort
        );
        hbSender.start();
    }
    
    /**
     * Inicializa a classe encarregada por receber packets UDP.
     */
    public static void startUdpListener(){
        udpReceiver = new UdpReceiver();
        udpReceiver.start();
    }
    
    /**
     * Estabelece uma ligação ao servidor TCP (FileServer)
     * @param servAddr
     * @param servPort 
     */
    public static void connectToTcpServer(InetAddress servAddr, int servPort){
        try {
            socketToServer = new Socket(servAddr, servPort);
            socketToServer.setSoTimeout(TIMEOUT*1000);  
            
        } catch(IOException e){
            System.out.println("Ocorreu um erro no acesso ao socket" + ":\n\t"+e);
        }finally{
            
            /*if(socketToServer != null){
                try {
                    socketToServer.close();
                } catch (IOException ex) {}
            } */
        }     
    }
    
    /**
     * Faz um pedido ao servidor TCP (FileServer).
     * @param request
     * @return response
     */
    public String askTcpServer(String request){
        PrintWriter pout;
        InputStream in;
        
        try {
            //int nbytes;
            //byte []fileChunck = new byte[MAX_SIZE];
            if(socketToServer == null) return null;
            
            in = socketToServer.getInputStream();
            pout = new PrintWriter(socketToServer.getOutputStream(), true);
            
            // Enviar um pedido ao servidor de ficheiros (TCP)
            pout.println(request);
            pout.flush();
            
            // Receber a resposta do servidor (TCP)
            
            //DEBUG - ver se o tcp está funcional
            //String resposta;
            BufferedReader in_ = new BufferedReader(new InputStreamReader(in));
            
            //resposta = in_.readLine();  
            return in_.readLine();
            //System.out.println("\n\t"+resposta);
            
            // fim Debug
        
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
