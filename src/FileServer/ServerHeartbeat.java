package FileServer;

import common.Heartbeat;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

/* Inlui os dados do heartbeat e os clientes ligados */

public class ServerHeartbeat extends Heartbeat implements Serializable{
    private ArrayList<String> connectedClients;
    
    public ServerHeartbeat(InetAddress addr, int p, String n, ArrayList<String> clients){
        super(addr,p,n);
        this.connectedClients = new ArrayList<String>(clients);
    }
    
    public ArrayList<String> getConnectedClients(){
        return connectedClients;
    }
}
