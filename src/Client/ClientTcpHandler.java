package Client;

import java.net.InetAddress;
import java.net.Socket;

public class ClientTcpHandler {
    protected InetAddress serverAddress;
    protected int serverPort;
    protected Socket socketToServer;
    
    public ClientTcpHandler(InetAddress servAddr, Integer servPort){
        serverAddress = servAddr;
        serverPort = servPort;
        
        socketToServer = new Socket();
    }
    
}
