package Client;

import common.Msg;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientCommands {
    
    public static final String LIST = "LIST";
    public static final String MSG = "MSG";
    
    private ClientTcpHandler tcpHandler;
    private ClientUdpHandler udpHandler;
    
    public ClientCommands(ClientUdpHandler udpHandler, ClientTcpHandler tcpHandler){
        this.tcpHandler = tcpHandler;
        this.udpHandler = udpHandler;
    }
    
    public String processRequest(Msg msg){
        String[] args = msg.getMsg().split("\\s");
        
        if (args[0].equalsIgnoreCase(LIST)
                || args[0].equalsIgnoreCase(MSG))
        {
            try {
                return udpHandler.sendRequest(msg);
            } catch (IOException ex) {
                Logger.getLogger(ClientCommands.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientCommands.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "";
    }
}
