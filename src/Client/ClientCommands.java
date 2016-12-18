package Client;

import common.Msg;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientCommands {
    
    private static final String LIST = "LIST";
    private static final String MSG = "MSG";
    private static final String USERS = "USERS";
    
    private ClientTcpHandler tcpHandler;
    private ClientUdpHandler udpHandler;
    
    public ClientCommands(ClientUdpHandler udpHandler, ClientTcpHandler tcpHandler){
        this.tcpHandler = tcpHandler;
        this.udpHandler = udpHandler;
    }
    
    public String processRequest(Msg msg) throws UnknownHostException, IOException{
        String[] args = msg.getMsg().split("\\s");
        
        if (args[0].equalsIgnoreCase(LIST)
            || args[0].equalsIgnoreCase(MSG)
            || args[0].equalsIgnoreCase(USERS))
        {
            try {
                return udpHandler.sendRequest(msg);
            } catch (IOException ex) {
                System.out.println("Erro ao enviar pedido UDP! " + ex);
            } catch (ClassNotFoundException ex) {
                System.out.println(ex);
            }
        }
        else if (args[0].equalsIgnoreCase("CONNECT")){
            if (args.length == 3){
                //connect 127.0.0.1 7001
                System.out.println("jkj");
                tcpHandler.connectToServer(InetAddress.getByName(args[1]), Integer.parseInt(args[2]));
                System.out.println(
                    tcpHandler.sendRequest("Pedido teste")
                );
            } else System.out.println("Erro de sintaxe: connect <ip> <porto>");
        }
        return "";
    }
}
