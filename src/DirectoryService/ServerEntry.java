package DirectoryService;

import FileServer.ServerHeartbeat;
import java.util.ArrayList;

public class ServerEntry extends common.Entry{
    
    private ArrayList<String> connectedClients;
    
    public ServerEntry(ServerHeartbeat hb){
        // Cria uma entrada de registo selando-a com o tempo actual.
        super(hb);
        connectedClients = hb.getConnectedClients();
    }
    
    public ArrayList<String> getConnectedClients(){
        return connectedClients;
    }
    
    public boolean existsClient(String c){
        return connectedClients.contains(c);
    }
}
