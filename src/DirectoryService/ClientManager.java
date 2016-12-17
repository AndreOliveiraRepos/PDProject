/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectoryService;

import common.Heartbeat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author luism
 */
public class ClientManager extends Thread {
    
    private static final int ACCEPTED_INTERVAL = 34; //seconds //34
    
    private HashMap<String,ClientEntry> onlineClients;
    private boolean running;
    
    public ClientManager(){
        onlineClients = new HashMap<String,ClientEntry>();
        running = true;
    }
    
    public void processHeartbeat(Heartbeat hb){
        ClientEntry ce = new ClientEntry(hb);
        onlineClients.put(ce.getName(),ce);
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
                    double timestampSeconds = (double)entry.getValue().getTimestamp() / 1000000000.0;
                    double systemSeconds = (double)System.nanoTime() / 1000000000.0;
                    if((systemSeconds - timestampSeconds) > ACCEPTED_INTERVAL){
                        it.remove();
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
}
