/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectoryService;

import common.Heartbeat;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClientManager extends Thread {
    
    private static final int ACCEPTED_INTERVAL = 34; //seconds //34
    
    private HashMap<String,ClientEntry> onlineClients;
    private boolean running;
    private boolean hasChanges;
    
    public ClientManager(){
        onlineClients = new HashMap<String,ClientEntry>();
        running = true;
        hasChanges = false;
    }
    
    public void processHeartbeat(Heartbeat hb, InetAddress hbAddr, ServerManager sm){
        Integer nClients = getNumAuthenticatedClients(sm);
        ClientEntry ce = new ClientEntry(hb, hbAddr);
        onlineClients.put(ce.getName(),ce);
        
        if(nClients.equals(getNumAuthenticatedClients(sm)))
            hasChanges = true;
    }
    
    public boolean hasChanges(){
        if (hasChanges){
            hasChanges = false;
            return true;
        }
        return false;
    }
    
    public int getNumAuthenticatedClients(ServerManager sm){
        int total = 0;
        for (String c : onlineClients.keySet()){
            if (sm.isAuthenticatedClient(c)) ++total;
        }
        return total;
    }
    
    public ClientEntry getClient(String c){
        return onlineClients.get(c);
    }
    
    @Override
    public void run(){
        while(running){
            if (!onlineClients.isEmpty())
            {
                Iterator it = onlineClients.entrySet().iterator();
                while(it.hasNext())
                {
                    Map.Entry<String,ClientEntry> entry = (Map.Entry)it.next();
                    //System.out.println("\t" + entry.getKey());
                    double timestampSeconds = (double)entry.getValue().getTimestamp() / 1000000000.0;
                    double systemSeconds = (double)System.nanoTime() / 1000000000.0;
                    if((systemSeconds - timestampSeconds) > ACCEPTED_INTERVAL){
                        it.remove();
                        hasChanges = true;
                    }
                }
            }
            try{
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                System.out.println("Erro na thread que gere os clientes! "+ex);
            }
        }
    }
    
    public HashMap<String,ClientEntry> getOnlineClients(){
        return onlineClients;
    }
    
    public void stopThread()
    {
        running = false;
    }
    
    public ArrayList<ClientEntry> getUserList(){
        //return onlineServers.values();
        ArrayList<ClientEntry> arr = new ArrayList<ClientEntry>();
        Iterator it = onlineClients.values().iterator();
        while(it.hasNext()) arr.add((ClientEntry)it.next());
        return arr;
    }
    
    public String getClientListAsString(ServerManager sm){
        Iterator it = onlineClients.keySet().iterator();
        StringBuilder clientsAsString = new StringBuilder();
        while (it.hasNext()){
            String c = (String)it.next();
            if (sm.isAuthenticatedClient(c)){
                clientsAsString.append(c + "\n");
            }
        }
        return clientsAsString.toString();
    }
}
