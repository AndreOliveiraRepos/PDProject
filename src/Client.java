
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
    public static final String EXIT = "EXIT";
    
    private static InetAddress directoryServerAddr;
    private static int directoryServerPort;
    private static String name;
    
    private static Socket socketToServer;       //Socket TCP
    private static DatagramSocket socketUDP;    //Socket UDP
    private static UdpListener udpListener;
    private static HeartbeatSender hbSender;
        
    public static void main(String[] args) {
        String msg;
        DatagramPacket packet;
        DatagramSocket socket;
        
        if(args.length != 2){
            System.out.println("Sintaxe: java Client dirAdress dirUdpPort");
            return;
        }
        
        name = "guest";
        
        try {
            
            // Inicializa socket UDP para ler e enviar mensagens
            directoryServerAddr = InetAddress.getByName(args[0]);
            directoryServerPort = Integer.parseInt(args[1]);
            socketUDP = new DatagramSocket();            
            
            // Fica á escuta no Porto UDP automático
            startUdpListener();
            
            // Enviar heartbeats UDP ao serviço de directoria
            beginHeartbeat();
            
            // Ligar ao servidor de ficheiros TCP
            connectToTcpServer(InetAddress.getByName("127.0.0.1"), 7001);
            System.out.println(askTcpServer(" pedido1"));
            
            // Tratar das mensagens da consola para ser enviadas ao serv. de directoria Udp
            ByteArrayOutputStream baos;
            ObjectOutputStream oOut;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            
            while(true){
                System.out.print("> ");
                msg = in.readLine();
                
                if(msg.equalsIgnoreCase(EXIT)){
                    break;
                }
                
                baos = new ByteArrayOutputStream();
                oOut = new ObjectOutputStream(baos);
                oOut.writeObject(new Msg(name, msg));
                oOut.flush(); oOut.close();
            
                packet = new DatagramPacket(baos.toByteArray(), baos.size(),
                        directoryServerAddr, directoryServerPort);
                socketUDP.send(packet);
            }
            closeTcpConnection();
            //E UDP TAMBÉM
            
            //Esperar que as threads terminem (n é preciso)
            //x.join();
            
        } catch (UnknownHostException ex) {
            System.out.println("[Cliente] Destino desconhecido:\n\t"+ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } /*catch (InterruptedException ex) {
            System.out.println("[Cliente-Heartbeat] Erro na thread UDP de cliente:\n\t"+ex);
        }*/
    }
    
    /**
     * Thread que fica encarregada de enviar o heartbeat de 30 em 30 segs.
     */
    public static void beginHeartbeat(){
        hbSender = new HeartbeatSender(
                new Heartbeat(udpListener.getLocalPort(),name),
                directoryServerAddr, directoryServerPort
        );
        hbSender.start();
    }
    
    /**
     * Inicializa a classe encarregada por receber packets UDP.
     */
    public static void startUdpListener(){
        udpListener = new UdpListener(socketUDP);
        udpListener.start();
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
        }
    }
    
    public static void closeTcpConnection(){
        if(socketToServer != null){
            try {
                socketToServer.close();
            } catch (IOException ex) {}
        }
    }
    
    /**
     * Faz um pedido ao servidor TCP (FileServer).
     * @param request
     * @return response
     */
    public static String askTcpServer(String request){
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
