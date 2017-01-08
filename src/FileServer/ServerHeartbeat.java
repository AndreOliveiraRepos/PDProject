package FileServer;

import common.Heartbeat;
import java.io.Serializable;
import java.util.ArrayList;

/* Inlui os dados do heartbeat e os clientes ligados */

public class ServerHeartbeat extends Heartbeat implements Serializable{
    
    static final long serialVersionUID = 10L;
    private ArrayList<String> connectedClients;
    
    public ServerHeartbeat(int p, String n, ArrayList<String> clients){
        super(p,n);
        this.connectedClients = new ArrayList<String>(clients);
    }
    
    public ArrayList<String> getConnectedClients(){
        return connectedClients;
    }
}
