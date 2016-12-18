/*
 * Classe que gere os servidores activos.
 */
package DirectoryService;

import FileServer.ServerHeartbeat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ServerManager extends Thread
{
    public static final int ACCEPTED_INTERVAL = 34; //seconds //34
    
    private HashMap<String,ServerEntry> onlineServers;
    private boolean running;
    
    public ServerManager()
    {
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
                    
                    System.out.print("" + entry.getKey() /*+ " = " + entry.getValue()*/);
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
    
    public Map<String,Integer> getServerMap(){
//        StringBuilder buffer = new StringBuilder();
//        Iterator it = onlineServers.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<String,ServerEntry> entry = (Map.Entry)it.next();  
//
//            System.out.print("" + entry.getKey() /*+ " = " + entry.getValue()*/);
//            buffer.append(entry.getKey() + " - " + entry.getValue().getPort() + "\n");
//        }
//        return buffer.toString();*/
        Map<String,Integer> serversInfo = new HashMap<String,Integer>();

        Iterator it = onlineServers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String,ServerEntry> entry = (Map.Entry)it.next();

            //System.out.print("" + entry.getKey() /*+ " = " + entry.getValue()*/);
            //buffer.append(entry.getKey() + " - " + entry.getValue().getPort() + "\n");
            serversInfo.put(entry.getKey(), entry.getValue().getPort());
        }
        return serversInfo;
    }
    
    @Override
    public String toString(){
        return onlineServers.toString();
    }
    
    public boolean isAuthenticatedClient(String c){
        Iterator it = onlineServers.values().iterator();
        while (it.hasNext()) {
            if(((ServerEntry)it.next()).existsClient(c)){
                return true;
            }
        }
        return false;
    }
}
