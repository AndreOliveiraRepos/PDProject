package DirectoryService;

import FileServer.ServerHeartbeat;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

public class ServerEntry extends common.Entry implements Serializable{
    
    private ArrayList<String> connectedClients;
    
    public ServerEntry(ServerHeartbeat hb, InetAddress hbAddr){
        // Cria uma entrada de registo selando-a com o tempo actual.
        super(hb, hbAddr);
        connectedClients = hb.getConnectedClients();
    }
    
    public ArrayList<String> getConnectedClients(){
        return connectedClients;
    }
    
    public boolean existsClient(String c){
        return connectedClients.contains(c);
    }
    
    @Override
    public String toString(){
        return ("" + name + "\t" + address.getHostAddress() + "\t" + port );
    }
}
