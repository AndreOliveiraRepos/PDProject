package Client;

import common.Msg;
import java.io.IOException;
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
    
    public String processRequest(Msg msg){
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
        return "";
    }
}
