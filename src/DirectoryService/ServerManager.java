/*
 * Classe que gere os servidores activos.
 */
package DirectoryService;

import FileServer.ServerHeartbeat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ServerManager extends Thread{
    public static final int ACCEPTED_INTERVAL = 20; //seconds //34
    
    protected HashMap<String,ServerEntry> onlineServers;
    protected boolean running;
    
    public ServerManager(){
        onlineServers = new HashMap<String,ServerEntry>();
        running = true;
    }
    
    public void processHeartbeat(ServerHeartbeat hb){
        ServerEntry se = new ServerEntry(hb);
        onlineServers.put(se.getName(),se);
    }
    
    @Override
    public void run(){
        while(running){
            if (!onlineServers.isEmpty()){
                Iterator it = onlineServers.entrySet().iterator();
                System.out.println("Online Servers: ");
                while (it.hasNext()) {
                    Map.Entry<String,ServerEntry> entry = (Map.Entry)it.next();  
                    
                    System.out.print(""+entry.getKey() /*+ " = " + entry.getValue()*/);
                    double timestampSeconds = (double)entry.getValue().getTimestamp() / 1000000000.0;
                    double systemSeconds = (double)System.nanoTime() / 1000000000.0;
                    System.out.print("\t Ultimo hb: "+(systemSeconds - timestampSeconds)+ " segundos.\n");
                    if((systemSeconds - timestampSeconds) > ACCEPTED_INTERVAL){
                        it.remove();
                        //onlineServers.remove(entry.getKey());
                    }
                    //it.remove(); //iterador
                }
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                System.out.println("Erro durante o sleep da thread que gere os servidores! "+ex);
            }
        }
    }
    
    public void stopThread(){
        running = false;
    }
    
    public List<ServerEntry> getOnlineServers(){
        return (List<ServerEntry>) onlineServers.values();
    }
    
    @Override
    public String toString(){
        return onlineServers.toString();
    }
}
