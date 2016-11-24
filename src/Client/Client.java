package Client;

import common.Msg;
import common.Heartbeat;
import common.HeartbeatSender;
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
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    public static final int MAX_SIZE = 4000;
    public static final int TIMEOUT = 5;
    public static final String EXIT = "EXIT";
   
    private static String name;
    
    private static Socket socketToServer;       //Socket TCP
    private static ClientUdpHandler udpHandler;
    private static HeartbeatSender hbSender;
        
    public static void main(String[] args) {
        String msg;
        
        if(args.length != 2){
            System.out.println("Sintaxe: java Client dirAdress dirUdpPort");
            return;
        }
        
        name = "guest";
        
        try {
            
            // Inicializa socket UDP para ler e enviar mensagens
            InetAddress directoryServerAddr = InetAddress.getByName(args[0]);
            Integer directoryServerPort = Integer.parseInt(args[1]);
            udpHandler = new ClientUdpHandler(directoryServerAddr, directoryServerPort);
            
            // Enviar heartbeats UDP ao serviço de directoria
            (hbSender = new HeartbeatSender(
                    new Heartbeat(udpHandler.getLocalPort(),name),directoryServerAddr, directoryServerPort)
            ).start();
            
            // Ligar ao servidor de ficheiros TCP
            connectToTcpServer(InetAddress.getByName("127.0.0.1"), 7001);
            
            
            System.out.println(askTcpServer(" pedido1"));
            
            /* Await Commands */    
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                System.out.print("> ");
                msg = in.readLine();

                if(msg.equalsIgnoreCase(EXIT)){ break; }
                
                // DISTINGUIR ENTRE COMANDO PARA O SERVIÇO DE DIRECTORIA E PARA O TCP
                //Imprime a resposta ao pedido do serviço de directoria
                //EXEMPLO
                System.out.println(
                        udpHandler.sendRequest(new Msg(name, msg))
                );
            }
            udpHandler.closeSocket();
            closeTcpConnection();
            

        } catch (UnknownHostException ex) {
            System.out.println("[Cliente] Destino desconhecido:\n\t"+ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }catch(ClassNotFoundException e){
             System.out.println("O objecto recebido não é do tipo esperado:\n\t"+e);
        }
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
            /* O servidor reve de devolver respostas baseadas nos comandos a ele enviados,
            deve também ser possível receber ficheiros nbytes = in.read(fileChunk);
            ver os exemplos do marinho
            */
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
