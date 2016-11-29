package Client;

import common.Msg;
import common.Heartbeat;
import common.HeartbeatSender;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    public static final String EXIT = "EXIT";
   
    private static String name;
    
    private static ClientTcpHandler tcpHandler;
    private static ClientUdpHandler udpHandler;
    private static HeartbeatSender<Heartbeat> hbSender;
        
    public static void main(String[] args) {
        String msg;
        
        if(args.length != 2){
            System.out.println("Sintaxe: java Client dirAdress dirUdpPort");
            return;
        }
        
        name = "guest";
        
        try {
            
            // Inicializa socket UDP para ler e enviar mensagens ao serviço de directoria
            InetAddress directoryServerAddr = InetAddress.getByName(args[0]);
            Integer directoryServerPort = Integer.parseInt(args[1]);
            udpHandler = new ClientUdpHandler(directoryServerAddr, directoryServerPort);
            
            tcpHandler = new ClientTcpHandler();
            
            // Enviar heartbeats UDP ao serviço de directoria
            hbSender = new HeartbeatSender<Heartbeat>(new Heartbeat(udpHandler.getLocalPort(),name),directoryServerAddr, directoryServerPort);
            hbSender.setDaemon(true);
            hbSender.start();
            
            // exemplo de uso request ao server TCP
            //tcpHandler.connectToServer(InetAddress.getByName("127.0.0.1"), 7001);
            /*System.out.println(
                    tcpHandler.sendRequest("I pedido1 I")
            );*/
            /* Exemplo para listagem de comandos
            NAME luis -> altera o nome para luis
            EXIT -> sai (já faz isto no while loop)
            LIST -> lista os servidores ligados
            MSG -> envia uma mensagem a todos os clientes activos
            MSGTO luis -> envia uma mensagem ao luis
            ... o resto dos comandos são enviados ao tcp
            */

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                System.out.print("> ");
                msg = in.readLine();

                if(msg.equalsIgnoreCase(EXIT)){ break; }
                
                // DISTINGUIR ENTRE COMANDO PARA O SERVIÇO DE DIRECTORIA E PARA O TCP
                //Imprime a resposta ao pedido do serviço de directoria
                //EXEMPLO
                // exemplo de uso request ao server UDP  
                System.out.println(
                        udpHandler.sendRequest(new Msg(name, msg))
                );
            }
            udpHandler.closeSocket();
            tcpHandler.closeSocket();
            
        } catch (UnknownHostException ex) {
            System.out.println("[Cliente] Destino desconhecido:\n\t"+ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }catch(ClassNotFoundException e){
             System.out.println("O objecto recebido não é do tipo esperado:\n\t"+e);
        }
    }
}